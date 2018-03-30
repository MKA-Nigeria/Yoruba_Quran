package com.alium.yoruba_quran.ui.base.presenters;


import com.alium.yoruba_quran.ui.base.view.BaseMvpView;

/**
 * This interface must be inherited from  {@link BasePresenter}
 * Created by Aliu Abdul-Mujeeb on 21/03/2018.
 */
public interface IBasePresenter<V extends BaseMvpView> {

    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();
    void attachView(V view);
    void detachView();
    boolean isViewAttached();
    void checkViewAttached();
}