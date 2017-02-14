package com.estsoft.muvigram.transcodeexample.Base;

/**
 * Created by estsoft on 2017-02-14.
 */

public abstract class BasePresenter<T extends MvpView> implements Presenter<T>{

    private T mMvpView;

    @Override public void detachView() {
        mMvpView = null;
    }

    @Override public void attachView(T mvpView) {
        mMvpView = mvpView;
    }

    @Override public boolean isViewAttached() {
        return mMvpView != null;
    }

    @Override
    public T getMvpView() {
        return mMvpView;
    }
}
