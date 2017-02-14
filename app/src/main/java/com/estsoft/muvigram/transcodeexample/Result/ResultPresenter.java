package com.estsoft.muvigram.transcodeexample.Result;

import android.content.Context;
import android.util.Log;

import com.estsoft.muvigram.transcodeexample.Base.BasePresenter;
import com.estsoft.muvigram.transcodeexample.Model.VideoData;
import com.estsoft.muvigram.transcodeexample.Transcoder.utils.TranscodeUtils;
import com.estsoft.muvigram.transcodeexample.Transcoder.wrappers.MediaEditor;
import com.estsoft.muvigram.transcodeexample.Transcoder.wrappers.ProgressListener;
import com.estsoft.muvigram.transcodeexample.Utils.Utils;

import java.util.List;

/**
 * Created by estsoft on 2017-02-14.
 */

public class ResultPresenter extends BasePresenter<ResultMvpView> {
    private static final String TAG = "ResultPresenter";
    private String mTmpStoredPath;

    public void startTranscode( List<VideoData> dataList, Context context ) {
        mTmpStoredPath = TranscodeUtils.getAppCashingFile( context );

        MediaEditor editor = new MediaEditor( mTmpStoredPath, MediaEditor.NORMAL, mTranscodeProgressListener );

        // I-Frame Interval : 1, FPS : 30, bitrate : 5M, video-rotation - 90, resolution : HD
        editor.initVideoTarget(1, 30, 5000000, 90, 1280, 720 );
        // audio-sampleRate : 44.1k, stereo, bitrate : 128k
        editor.initAudioTarget(44100, 2, 128 * 1000);

        for ( VideoData data : dataList ) {
            String path = data.videoPath;
            long videoDuration = Utils.getVideoDuration( path );
            double cutoffPercentage = (double)data.cutOffPercent / 200.0d;
            long start = (long)((double)videoDuration * cutoffPercentage);
            long end = (long)((double)videoDuration * ( 1 - cutoffPercentage ));
            Log.d(TAG, "startTranscode: " + start + " / " + end + " / " + videoDuration);
            // Milli sec to Micro sec
            editor.addSegment( path, start * 1000, end * 1000, 100 );
        }

        new Thread(() -> editor.startWork()).start();

    }

    private void setProgressInUiThread( String msg ) {
        getMvpView().updateProgress( msg );
    }
    private void setVideoAndStart() {
        getMvpView().videoStart( mTmpStoredPath );
    }

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
            setVideoAndStart();
        }

        @Override
        public void onError(Exception exception) {
        }
    };

}
