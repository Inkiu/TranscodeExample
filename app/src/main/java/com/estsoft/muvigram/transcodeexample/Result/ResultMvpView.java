package com.estsoft.muvigram.transcodeexample.Result;

import com.estsoft.muvigram.transcodeexample.Base.MvpView;

/**
 * Created by estsoft on 2017-02-14.
 */

public interface ResultMvpView extends MvpView {

    void updateProgress( String msg );
    void videoStart( String path );
}
