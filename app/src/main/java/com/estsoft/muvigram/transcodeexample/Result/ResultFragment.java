package com.estsoft.muvigram.transcodeexample.Result;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.estsoft.muvigram.transcodeexample.Model.VideoData;
import com.estsoft.muvigram.transcodeexample.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ResultFragment extends Fragment implements ResultMvpView {
    private static final String TAG = "ResultFragment";
    private final static String ARG_VIDEO_DATA = "ResultFragment.video_data";
    private long MAIN_THREAD_ID;

    public static ResultFragment newInstance(ArrayList<VideoData> datas ) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList( ARG_VIDEO_DATA, datas );
        fragment.setArguments(args);
        return fragment;
    }

    private List<VideoData> mVideoData;
    private ResultPresenter mPresenter;

    @BindView(R.id.result_text_progress) TextView mProgressText;
    @BindView(R.id.result_video) VideoView mResultVideoView;
    Unbinder mUnbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MAIN_THREAD_ID = Thread.currentThread().getId();
        if (getArguments() != null) {
            mVideoData = getArguments().getParcelableArrayList( ARG_VIDEO_DATA );
        }

        mPresenter = new ResultPresenter();
        mPresenter.attachView( this );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.startTranscode( mVideoData, getContext() );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView();
        mUnbinder.unbind();
    }

    @Override
    public void updateProgress(String msg) {
        if ( isOnUiThread() ) {
            getActivity().runOnUiThread(() -> mProgressText.setText( msg ));
        } else {
            mProgressText.setText( msg );
        }
    }

    @Override
    public void videoStart(String path) {
        if ( isOnUiThread() ) {
            getActivity().runOnUiThread(() -> {
                mResultVideoView.setVideoPath(path);
                mResultVideoView.setOnCompletionListener(mediaPlayer -> mResultVideoView.start());
                mResultVideoView.setOnPreparedListener(mediaPlayer -> mResultVideoView.start());
            });
        } else {
            mResultVideoView.setVideoPath(path);
            mResultVideoView.setOnCompletionListener(mediaPlayer -> mResultVideoView.start());
            mResultVideoView.setOnPreparedListener(mediaPlayer -> mResultVideoView.start());
        }
    }

    private boolean isOnUiThread () {
        return Thread.currentThread().getId() != MAIN_THREAD_ID;
    }
}
