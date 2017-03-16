package com.lucasurbas.masterdetail.ui.chapters;

import com.lucasurbas.masterdetail.data.Chapter;
import com.lucasurbas.masterdetail.ui.util.BaseNavigator;
import com.lucasurbas.masterdetail.ui.util.BasePresenter;
import com.lucasurbas.masterdetail.ui.util.BaseView;

import java.util.List;

/**
 * Created by Lucas on 04/01/2017.
 */

public interface ChapterContract {

    interface Navigator extends BaseNavigator {

        void goToPersonDetails(Chapter chapter);
    }

    interface View extends BaseView {

        void showLoading();

        void hideLoading();

        void showPeopleList(List<Chapter> peopleList);

        void showToast(String message);
    }

    interface Presenter extends BasePresenter<ChapterContract.View> {

        void getPeople();

        void clickPerson(Chapter chapter);

        void clickPersonAction(Chapter chapter);

        void loadMorePeople();
    }
}
