package com.alium.yoruba_quran.ui.main;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alium.yoruba_quran.R;
import com.alium.yoruba_quran.injection.app.ApplicationComponent;
import com.alium.yoruba_quran.injection.main.DaggerMainComponent;
import com.alium.yoruba_quran.injection.main.MainComponent;
import com.alium.yoruba_quran.injection.main.MainModule;
import com.alium.yoruba_quran.ui.PresentationConstants;
import com.alium.yoruba_quran.ui.download.DownloaderService;
import com.alium.yoruba_quran.ui.util.BaseActivity;
import com.alium.yoruba_quran.ui.warning_fragment.AlertFragment;
import com.alium.yoruba_quran.ui.widget.ContainersLayout;
import com.alium.yoruba_quran.ui.widget.CustomAppBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    String DOWNLOAD_INSTRUCTIONS_TAG = "DOWNLOAD_INSTRUCTIONS";
    String DOWNLOAD_SUCCESS_TAG = "DOWNLOAD_SUCCESS_TAG";
    String DOWNLOAD_FAILURE_TAG = "DOWNLOAD_FAILURE_TAG";

    @Inject
    MainContract.Presenter presenter;
    @Inject
    MainContract.Navigator navigator;

    @BindView(R.id.activity_main__nav)
    NavigationView navigationView;
    @Nullable
    NavigationView navigationSideView;
    @Nullable
    @BindView(R.id.activity_main__insets)
    ViewGroup insetsView;
    @BindView(R.id.activity_main__drawer)
    DrawerLayout drawer;
    @BindView(R.id.activity_main__custom_appbar)
    CustomAppBar customAppBar;
    @BindView(R.id.activity_main__containers_layout)
    ContainersLayout containersLayout;

    private MainComponent mainComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (insetsView != null && navigationSideView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(insetsView, new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    ((ViewGroup.MarginLayoutParams) insetsView.getLayoutParams()).topMargin = insets.getSystemWindowInsetTop();
                    ((ViewGroup.MarginLayoutParams) insetsView.getLayoutParams()).bottomMargin = insets.getSystemWindowInsetBottom();
                    insetsView.requestLayout();
                    ((ViewGroup.MarginLayoutParams) navigationSideView.getLayoutParams()).topMargin = (-insets.getSystemWindowInsetTop());
                    navigationSideView.requestLayout();
                    return insets.consumeSystemWindowInsets();
                }
            });
            navigationSideView.setNavigationItemSelectedListener(this);
        }

        navigationView.setNavigationItemSelectedListener(this);
        customAppBar.setOnNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        presenter.attachView(this);
        if (savedInstanceState == null) {
            presenter.onCreate();
            presenter.clickPeople();
        }
    }

    @Override
    public void injectDependencies() {

    }

    @Override
    public void attachToPresenter() {

    }

    @Override
    public void detachFromPresenter() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onLandscape() {

    }

    @Override
    public void onPortrait() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showNoNetwork() {

    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean checkIfFilesExist(String filePath) {
        return fileUtils.checkExistsFile(filePath);
    }

    @Override
    public void showDownloadInstructionDialog() {
        FragmentManager manager = getFragmentManager();
        AlertFragment fragment1 = AlertFragment.newInstance("Large Download",
                "This app requires that you download some data files, " +
                        "it is recoomended that you continue on an un-metered wifi connection",DOWNLOAD_INSTRUCTIONS_TAG);
        fragment1.show(manager, DOWNLOAD_INSTRUCTIONS_TAG);
    }


    @Override
    public void startDownloadService(String url) {
        Intent intent = new Intent(getContext(), DownloaderService.class);
        intent.putExtra(PresentationConstants.FILE_URL_EXTRA, url);
        startService(intent);
    }

    @Override
    public void showDownloadSuccessDialog() {
        FragmentManager manager = getFragmentManager();
        AlertFragment fragment1 = AlertFragment.newInstance("Download Completed",
                "The required files have been downloaded",DOWNLOAD_SUCCESS_TAG);
        fragment1.show(manager, DOWNLOAD_SUCCESS_TAG);
    }

    @Override
    public void showDownloadFailedDialog() {
        FragmentManager manager = getFragmentManager();
        AlertFragment fragment1 = AlertFragment.newInstance("Download Failed",
                "The download failed to complete, please retry",DOWNLOAD_FAILURE_TAG);
        fragment1.setButtonClickListener(new AlertFragment.OnButtonClickListener() {
            @Override
            public void onButtonClicked(String tag) {
                presenter.onResume();
            }
        });
        fragment1.show(manager, DOWNLOAD_FAILURE_TAG);
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        mainComponent = DaggerMainComponent.builder()
                .applicationComponent(applicationComponent)
                .mainModule(new MainModule(this))
                .build();

        mainComponent.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter.onDestroy();
    }

    @Override
    public void closeDrawer() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }, 100);
        }
    }

    @Override
    public void openDrawer() {
        if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void highlightHomeFeed() {
        navigationView.setCheckedItem(R.id.menu_main_nav__settings);
    }

    public void toggleDrawer() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }


    @Override
    public void highlightPeople() {
        navigationView.setCheckedItem(R.id.menu_main_nav__people);
        if (navigationSideView != null) {
            navigationSideView.setCheckedItem(R.id.menu_main_nav__people);
        }
    }

    @Override
    public void highlightFavorites() {
        navigationView.setCheckedItem(R.id.menu_main_nav__favorites);
        if (navigationSideView != null) {
            navigationSideView.setCheckedItem(R.id.menu_main_nav__favorites);
        }
    }

    @Override
    public void highlightSettings() {
        navigationView.setCheckedItem(R.id.menu_main_nav__settings);
    }

    @Override
    public void highlightFeedback() {
        navigationView.setCheckedItem(R.id.menu_main_nav__feedback);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_nav__people:
                presenter.clickPeople();
                break;


            case R.id.menu_main_nav__favorites:
                presenter.clickFavorites();
                break;


            case R.id.menu_main_nav__settings:
                presenter.clickSettings();
                break;

            case R.id.menu_main_nav__feedback:
                presenter.clickFeedback();
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!navigator.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public CustomAppBar getCustomAppBar() {
        return customAppBar;
    }

    public ContainersLayout getContainersLayout() {
        return containersLayout;
    }

    public MainContract.Navigator getNavigator() {
        return navigator;
    }

    public MainComponent getMainComponent() {
        return mainComponent;
    }

}
