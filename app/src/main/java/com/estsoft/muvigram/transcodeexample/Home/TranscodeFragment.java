package com.estsoft.muvigram.transcodeexample.Home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.estsoft.muvigram.transcodeexample.Models.VideoData;
import com.estsoft.muvigram.transcodeexample.R;
import com.estsoft.muvigram.transcodeexample.Transcoder.utils.TranscodeUtils;
import com.estsoft.muvigram.transcodeexample.Transcoder.wrappers.MediaEditor;
import com.estsoft.muvigram.transcodeexample.Transcoder.wrappers.ProgressListener;
import com.estsoft.muvigram.transcodeexample.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TranscodeFragment extends Fragment {
    private static final String TAG = "TranscodeFragment";
    private static final String ARG_VIDEO_DATAS = "TranscodeFragment.video_data";

    private VideoData[] mVideoDatas;
    private List<String> mVideoPaths = new ArrayList<>();
    private List<Long> mVideoStarts = new ArrayList<>();
    private List<Long> mVideoEnds = new ArrayList<>();
    private String mTmpStorePath;

    public static TranscodeFragment newInstance(VideoData[] videoDatas ) {
        TranscodeFragment fragment = new TranscodeFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_VIDEO_DATAS, videoDatas );
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.fragment_background) LinearLayout mBackGround;
    @BindView(R.id.video_view) VideoView mVideoView;
    @BindView(R.id.text_progress)TextView mProgressText;
    Unbinder mUnbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideoDatas = (VideoData[])getArguments().getParcelableArray(ARG_VIDEO_DATAS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transcode, container, false);
        mUnbinder = ButterKnife.bind( this, view );
        mBackGround.setOnTouchListener( (view1, motionEvent) -> true );
        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        translateVideoData();

        mTmpStorePath = TranscodeUtils.getAppCashingFile( getActivity() );

        MediaEditor editor = new MediaEditor( mTmpStorePath, MediaEditor.NORMAL, mTranscodeProgressListener );

        // I-Frame Interval : 1, FPS : 30, bitrate : 5M, video-rotation - 90, resolution : HD
        editor.initVideoTarget(1, 30, 5000000, 90, 1280, 720 );
        // audio-sampleRate : 44.1k, stereo, bitrate : 128k
        editor.initAudioTarget(44100, 2, 128 * 1000);

        for ( int i = 0; i < mVideoPaths.size(); i ++ ) {
            editor.addSegment( mVideoPaths.get(i), mVideoStarts.get(i), mVideoEnds.get(i), 100 );
        }

        new Thread (() -> {
            editor.startWork();
        }).start();

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void videoSetAndStart() {
        mVideoView.setVideoPath( mTmpStorePath );
        mVideoView.setOnCompletionListener( mediaPlayer ->  mVideoView.start()  );
        mVideoView.setOnPreparedListener(mediaPlayer -> mVideoView.start() );
    }


    private void translateVideoData() {
        for ( VideoData data : mVideoDatas ) {
            mVideoPaths.add( data.videoPath );

            // Milli sec to Micro sec
            Long videoDuration = Utils.getVideoDuration( data.videoPath ) * 1000;
            double cutOff = (double)data.cutOffPercent / 200.0d ;

            long start = (long)((double)videoDuration * cutOff);
            mVideoStarts.add(start);

            long end = (long)((double)videoDuration * ( 1 - cutOff ));
            mVideoEnds.add(end);

        }
    }

    private void setProgressInUiThread( String msg ) {
        getActivity().runOnUiThread(() ->
            mProgressText.setText( msg )
        );
    }

    /* listeners */
    private ProgressListener mTranscodeProgressListener = new ProgressListener() {
        @Override
        public void onStart(long estimatedDurationUs) {
            setProgressInUiThread( 0 + "%" );
        }

        @Override
        public void onProgress(long currentDurationUs, int percentage) {
            setProgressInUiThread( percentage + "%" );
        }

        @Override
        public void onComplete(long totalDuration) {
            setProgressInUiThread(100 + "% FINISHED" );
            getActivity().runOnUiThread(() -> {
                videoSetAndStart();
            });
        }

        @Override
        public void onError(Exception exception) {
        }
    };

}
