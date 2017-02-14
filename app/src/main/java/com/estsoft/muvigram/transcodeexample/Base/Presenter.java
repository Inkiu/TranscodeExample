package com.estsoft.muvigram.transcodeexample.Base;

/**
 * Created by estsoft on 2017-02-14.
 */

public interface Presenter<V extends MvpView> {

    void attachView( V mvpView );
    void detachView();
    boolean isViewAttached();
    V getMvpView();

}
