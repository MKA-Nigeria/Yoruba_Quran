package com.alium.yoruba_quran.ui.chapters;

import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_domain.entities.ChapterEntity;
import com.alium.yoruba_quran.ui.base.presenters.BasePresenter;
import com.alium.yoruba_quran.ui.base.presenters.IBasePresenter;
import com.alium.yoruba_quran.ui.base.view.BaseMvpView;
import com.alium.yoruba_quran.ui.util.BaseNavigator;

import java.util.List;

/**
 * Created by Lucas on 04/01/2017.
 */

public interface ChapterContract {

    interface Navigator extends BaseNavigator {

        void goToPersonDetails(ChapterEntity chapter);
    }

    interface View extends BaseMvpView {

        void showLoading();

        void hideLoading();

        void showPeopleList(List<ChapterEntity> peopleList);

        void showToast(String message);
    }

    interface Presenter extends IBasePresenter<View> {

        void getPeople();

        void clickPerson(ChapterEntity chapter);

        void clickPersonAction(ChapterEntity chapter);

        void onGetPeopleSuccess(List<ChapterEntity> chapters);

        void onGetPeopleFailure(Throwable error);
    }
}
