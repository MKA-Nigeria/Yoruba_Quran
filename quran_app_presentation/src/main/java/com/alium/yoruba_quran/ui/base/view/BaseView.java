package com.alium.yoruba_quran.ui.base.view;

import android.content.Context;

public interface BaseView {

    void injectDependencies();

    void attachToPresenter();
    void detachFromPresenter();

    void onLandscape();
    void onPortrait();

    void showLoading();
    void hideLoading();

    void showMessage(String message);
    void showNoNetwork();

    Context getContext();
}