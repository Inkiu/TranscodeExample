package com.estsoft.muvigram.transcodeexample.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estsoft.muvigram.transcodeexample.Model.VideoData;
import com.estsoft.muvigram.transcodeexample.R;
import com.estsoft.muvigram.transcodeexample.Result.ResultActivity;
import com.estsoft.muvigram.transcodeexample.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainFragment extends Fragment implements MainMvpView {
    private static final String TAG = "MainFragment";

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    private List<SeekBar> mSeekBars = new ArrayList<>();
    private MainPresenter mPresenter;

    @BindView(R.id.gallery_button)    Button mVideoSelectButton;
    @BindView(R.id.progress_container)    LinearLayout mProgressContainer;
    @OnClick(R.id.gallery_button) public void onGalleryButtonClicked() { mPresenter.requestVideoFromGallery(); }
    @OnClick(R.id.confirm_button) public void onConfirmButtonClicked() { prepareTranscode(); }
    Unbinder mUnbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new MainPresenter();
        mPresenter.attachView( this );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_main, container, false );
        mUnbinder = ButterKnife.bind( this, view );
        return view;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        String filePath = Utils.uriParseToAbsolutePath( getContext(), data.getData() );
        mPresenter.convertToFilePathAndStoreCutOff( filePath, 0 );
        String[] split = filePath.split( File.separator );
        inflateVideoController( split[ split.length -1 ] );
    }

    @Override
    public void makeToast(String msg, int length) {
        Toast.makeText( getActivity(), msg, length ).show();
    }

    @Override
    public void startVideoRequest(Intent intent, String msg, int REQUEST_CODE) {
        startActivityForResult( Intent.createChooser(intent, msg), REQUEST_CODE );
    }

    @Override
    public void startActivity( ArrayList<VideoData> datas ) {
        Intent intent = ResultActivity.getIntent( getActivity(), datas );
        getActivity().startActivity( intent );
    }

    private void prepareTranscode() {
        List<Integer> cutOffList = new ArrayList<>();
        for ( SeekBar seekBar : mSeekBars  ) {
            Log.d(TAG, "prepareTranscode: " + seekBar.getProgress() );
            // NOTE Too short video running time may occur Exception
            cutOffList.add( seekBar.getProgress() > 90 ? 90 : seekBar.getProgress() );
        }
        mPresenter.prepareTranscodeAndStartActivity( cutOffList );
    }

    private void inflateVideoController( String fileName ) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_video, mProgressContainer, false);
        mProgressContainer.addView(view);
        TextView title = (TextView)view.findViewById(R.id.item_video_title);
        title.setText( fileName );
        SeekBar seekbar = (SeekBar)view.findViewById(R.id.item_seek_bar);
        seekbar.setMax( 100 );
        mSeekBars.add(seekbar);
    }

}
