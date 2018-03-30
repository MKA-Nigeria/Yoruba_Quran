package com.alium.yoruba_quran.ui.main;


import com.alium.quran_app_domain.entities.ChapterEntity;
import com.alium.yoruba_quran.ui.base.presenters.BasePresenter;
import com.alium.yoruba_quran.ui.base.presenters.IBasePresenter;
import com.alium.yoruba_quran.ui.base.view.BaseMvpView;
import com.alium.yoruba_quran.ui.download.DownloadState;
import com.alium.yoruba_quran.ui.util.BaseNavigator;

/**
 * Created by Lucas on 12/06/16.
 */
public interface MainContract {

    interface Navigator extends BaseNavigator {

        void goToHomeFeed();

        void goToPeople();

        void goToPersonDetails(ChapterEntity chapter);

        void goToFavorites();

        void goToMap();

        void goToSettings();

        void goToFeedback();

        boolean onBackPressed();
    }

    interface View extends BaseMvpView {

        void closeDrawer();

        void openDrawer();

        void highlightHomeFeed();

        void highlightPeople();

        void highlightFavorites();

        void highlightSettings();

        void highlightFeedback();

        boolean checkIfFilesExist(String filePath);

        void showDownloadInstructionDialog();

        void startDownloadService(String url);

        void showDownloadSuccessDialog();

        void showDownloadFailedDialog();
    }

    interface Presenter extends IBasePresenter<View> {

        void clickHomeFeed();

        void clickPeople();

        void clickFavorites();

        void onDownloadPathRetrievalSuccess(String fileURl);

        void onDownloadPathRetrievalFailure(Throwable throwable);

        void clickMap();

        void clickSettings();

        void clickFeedback();

        void onDownloadStatusChanged(DownloadState downloadState);
    }
}
