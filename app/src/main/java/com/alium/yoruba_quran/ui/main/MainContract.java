package com.alium.yoruba_quran.ui.main;


import com.alium.yoruba_quran.ui.util.BaseView;
import com.alium.yoruba_quran.data.Chapter;
import com.alium.yoruba_quran.ui.util.BaseNavigator;
import com.alium.yoruba_quran.ui.util.BasePresenter;

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
