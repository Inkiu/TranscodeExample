package com.estsoft.muvigram.transcodeexample.Main;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.estsoft.muvigram.transcodeexample.Base.BasePresenter;
import com.estsoft.muvigram.transcodeexample.Model.VideoData;
import com.estsoft.muvigram.transcodeexample.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by estsoft on 2017-02-14.
 */

public class MainPresenter extends BasePresenter<MainMvpView>{
    public final int REQUEST_VIDEO_CODE = 2;
    private List<String> mSelectedVideoPaths = new ArrayList<>();
    private List<Integer> mSelectedVideoCutOff;

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


    public void prepareTranscodeAndStartActivity( List<Integer> cutOffs ) {
        mSelectedVideoCutOff = cutOffs;
        ArrayList<VideoData> datas = new ArrayList<>();
        for ( int i = 0; i < mSelectedVideoPaths.size(); i ++ ) {
            datas.add(new VideoData(mSelectedVideoCutOff.get(i), mSelectedVideoPaths.get(i)));
        }
        getMvpView().startActivity( datas );
    }

    public void convertToFilePathAndStoreCutOff ( String filePath, int percentage ) {
        mSelectedVideoPaths.add(filePath);
//        mSelectedVideoCutOff.add(percentage);
    }

    public void requestVideoFromGallery() {
        if (mSelectedVideoPaths.size() >= 5) {
            getMvpView().makeToast( "You can't select 5 more videos ", Toast.LENGTH_SHORT );
            return;
        }
        Intent intent = new Intent();
        intent.setType( "video/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        getMvpView().startVideoRequest( intent, "Select Video", REQUEST_VIDEO_CODE );
    }
}
