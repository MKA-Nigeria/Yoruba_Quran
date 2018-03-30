package com.alium.yoruba_quran.ui.base.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alium.yoruba_quran.ui.util.SnackBarUtils;


/**
 * This class must implement {@link BaseView}
 * Created by aliumujib on 18/05/2016.
 */
public abstract class BaseFragment
        extends Fragment
        implements BaseView {

    public AppCompatActivity getFragmentActivity() {
        return (AppCompatActivity) getActivity();
    }
    public Fragment getFragment() {
        return this;
    }
    public abstract void getArgs(Bundle _bundle);

    public Context getContext() {
        return getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.injectDependencies();
        this.attachToPresenter();
        super.onAttach(context);

    }

    @Override
    public void attachToPresenter() {

    }

    @Override
    public void detachFromPresenter() {

    }



    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {
        SnackBarUtils.showSimpleSnackbar(getRootView(), message);
    }

    public abstract View getRootView();

    @Override
    public void onDetach() {
        detachFromPresenter();
        super.onDetach();
    }

    public interface OnShowMessageListener {
        void onShowMessage(String message);
    }

    public interface OnChangeFragment {
        void onChangeFragment(String fragmentTag,
                              @Nullable Pair<View, String> sharedElement);
    }
}