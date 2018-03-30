package com.alium.yoruba_quran.ui.main;

import android.os.Environment;

import com.alium.quran_app_domain.base.Params;
import com.alium.quran_app_domain.interactors.GetDownloadURLUseCase;
import com.alium.quran_app_domain.rx.DefaultObserver;
import com.alium.yoruba_quran.ui.PresentationConstants;
import com.alium.yoruba_quran.ui.base.presenters.BasePresenter;
import com.alium.yoruba_quran.ui.download.DownloadState;
import com.alium.yoruba_quran.ui.util.eventbus.RxBus;

import javax.inject.Inject;

/**
 * Created by Lucas on 02/01/2017.
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private MainContract.Navigator navigator;
    private GetDownloadURLUseCase getDownloadURLUseCase;
    private boolean isCurrentlyDownloading;

    @Inject
    public MainPresenter(MainContract.Navigator navigator, GetDownloadURLUseCase getDownloadURLUseCase) {
        this.navigator = navigator;
        this.getDownloadURLUseCase = getDownloadURLUseCase;
    }

    @Override
    public void attachView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void clickHomeFeed() {
        if (view != null) {
            view.closeDrawer();
        }
        navigator.goToHomeFeed();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        RxBus.getInstance().observe(DownloadState.class).subscribe(new DownloadStatusObserver());
    }

    @Override
    public void onResume() {
        super.onResume();
        String fileURl = Environment.getExternalStorageDirectory() + PresentationConstants.TEMP_FOLDER_NAME + "/" + "YORUBA_QURAN.zip";
        if (!view.checkIfFilesExist(fileURl)) {
            if (!isCurrentlyDownloading) {
                isCurrentlyDownloading = true;
                view.showDownloadInstructionDialog();
                getDownloadURLUseCase.execute(new DownloadURLObserver(), Params.EMPTY);
            }
        } else {
            //SAY NADA .. NADA!! NADA!! NADA!!
        }
    }

    @Override
    public void clickPeople() {
        if (view != null) {
            view.highlightPeople();
            view.closeDrawer();
        }
        navigator.goToPeople();
    }

    @Override
    public void clickFavorites() {
        if (view != null) {
            view.highlightFavorites();
            view.closeDrawer();
        }
        navigator.goToFavorites();
    }

    @Override
    public void clickMap() {
        if (view != null) {
            view.closeDrawer();
        }
        navigator.goToMap();
    }

    @Override
    public void clickSettings() {
        if (view != null) {
            view.highlightSettings();
            view.closeDrawer();
        }
        navigator.goToSettings();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getDownloadURLUseCase.dispose();

    }

    @Override
    public void onDownloadPathRetrievalFailure(Throwable throwable) {
        view.showMessage(throwable.getMessage());
    }


    @Override
    public void onDownloadPathRetrievalSuccess(String fileURl) {
        view.startDownloadService(fileURl);
    }

    @Override
    public void clickFeedback() {
        if (view != null) {
            view.highlightFeedback();
            view.closeDrawer();
        }
        navigator.goToFeedback();
    }


    @Override
    public void onDownloadStatusChanged(DownloadState downloadState) {
        isCurrentlyDownloading = false;

        //View might have been detached
        if (view != null) {
            if (downloadState.getStatus().equals(DownloadState.Status.FAILED)) {
                view.showDownloadFailedDialog();
            } else {
                view.showDownloadSuccessDialog();
            }
        }
    }


    class DownloadURLObserver extends DefaultObserver<String> {
        @Override
        public void onNext(String s) {
            super.onNext(s);
            onDownloadPathRetrievalSuccess(s);
        }

        @Override
        public void onError(Throwable exception) {
            super.onError(exception);
            onDownloadPathRetrievalFailure(exception);
        }
    }

    class DownloadStatusObserver extends DefaultObserver<DownloadState> {
        @Override
        public void onNext(DownloadState downloadState) {
            super.onNext(downloadState);
            onDownloadStatusChanged(downloadState);
        }
    }

}
