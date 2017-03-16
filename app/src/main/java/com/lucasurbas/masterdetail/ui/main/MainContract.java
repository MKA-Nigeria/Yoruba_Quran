package com.lucasurbas.masterdetail.ui.main;


import com.lucasurbas.masterdetail.data.Chapter;
import com.lucasurbas.masterdetail.ui.util.BaseNavigator;
import com.lucasurbas.masterdetail.ui.util.BasePresenter;
import com.lucasurbas.masterdetail.ui.util.BaseView;

/**
 * Created by Lucas on 12/06/16.
 */
public interface MainContract {

    interface Navigator extends BaseNavigator {

        void goToHomeFeed();

        void goToPeople();

        void goToPersonDetails(Chapter chapter);

        void goToFavorites();

        void goToMap();

        void goToSettings();

        void goToFeedback();

        boolean onBackPressed();
    }

    interface View extends BaseView {

        void closeDrawer();

        void openDrawer();

        void highlightHomeFeed();

        void highlightPeople();

        void highlightFavorites();

        void highlightSettings();

        void highlightFeedback();

    }

    interface Presenter extends BasePresenter<View> {

        void clickHomeFeed();

        void clickPeople();

        void clickFavorites();

        void clickMap();

        void clickSettings();

        void clickFeedback();
    }
}
