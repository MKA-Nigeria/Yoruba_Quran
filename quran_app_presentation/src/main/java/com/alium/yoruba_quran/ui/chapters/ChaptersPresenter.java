package com.alium.yoruba_quran.ui.chapters;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_domain.base.Params;
import com.alium.quran_app_domain.entities.ChapterEntity;
import com.alium.quran_app_domain.interactors.GetAllChaptersUseCase;
import com.alium.quran_app_domain.rx.DefaultObserver;
import com.alium.yoruba_quran.ui.PresentationConstants;
import com.alium.yoruba_quran.ui.base.presenters.BasePresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by Lucas on 04/01/2017.
 */

public class ChaptersPresenter extends BasePresenter<ChapterContract.View> implements ChapterContract.Presenter {

    private Context context;
    private GetAllChaptersUseCase getAllChaptersUseCase;
    private ChapterContract.Navigator navigator;

    private List<ChapterEntity> chapterList = new ArrayList<>();
    private List<String> names;

    @Inject
    public ChaptersPresenter(Context context, ChapterContract.Navigator navigator, GetAllChaptersUseCase getAllChaptersUseCase) {
        this.navigator = navigator;
        this.context = context;
        this.getAllChaptersUseCase = getAllChaptersUseCase;
    }

    @Override
    public void onCreate() {
        this.getPeople();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void attachView(ChapterContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public boolean isViewAttached() {
        return false;
    }

    @Override
    public void checkViewAttached() {

    }

    @Override
    public void getPeople() {
        view.showLoading();
        getAllChaptersUseCase.execute(new ChaptersObserver(), Params.EMPTY);
    }



    private String getRandomName() {
        Random r = new Random();
        return names.get(r.nextInt(names.size()));
    }

    @Override
    public void clickPerson(ChapterEntity chapter) {
        final String fileURl = Environment.getExternalStorageDirectory() +PresentationConstants.TEMP_FOLDER_NAME + PresentationConstants.FOLDER_NAME + "/" + chapter.getIndexID() + "YOR.PDF";
        File file = new File(fileURl);
        if (file.exists()) {
            navigator.goToPersonDetails(chapter);
        } else {
            view.showToast("Quran File for " + chapter.getName() + " not found, please download data files!");
        }
    }

    @Override
    public void clickPersonAction(ChapterEntity chapter) {
        view.showToast("Action clicked: " + chapter.getName());
    }

    @Override
    public void onGetPeopleSuccess(List<ChapterEntity> chapters) {
        view.hideLoading();
        view.showPeopleList(chapters);
    }

    @Override
    public void onGetPeopleFailure(Throwable error) {
        view.hideLoading();
        error.printStackTrace();
        view.showMessage(error.getMessage());
    }


    class ChaptersObserver extends DefaultObserver<List<ChapterEntity>> {
        @Override
        public void onNext(List<ChapterEntity> chapters) {
            super.onNext(chapters);

            onGetPeopleSuccess(chapters);
        }

        @Override
        public void onError(Throwable exception) {
            super.onError(exception);

            onGetPeopleFailure(exception);
        }
    }
}
