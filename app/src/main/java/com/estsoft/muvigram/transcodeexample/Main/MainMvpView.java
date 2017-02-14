package com.estsoft.muvigram.transcodeexample.Main;

import android.content.Intent;
import android.net.Uri;

import com.estsoft.muvigram.transcodeexample.Base.MvpView;
import com.estsoft.muvigram.transcodeexample.Model.VideoData;

import java.util.ArrayList;

/**
 * Created by estsoft on 2017-02-14.
 */

public interface MainMvpView extends MvpView {

    void makeToast( String msg, int length );
    void startVideoRequest(Intent intent, String msg, int REQUEST_CODE );
    void startActivity( ArrayList<VideoData> datas );
}
