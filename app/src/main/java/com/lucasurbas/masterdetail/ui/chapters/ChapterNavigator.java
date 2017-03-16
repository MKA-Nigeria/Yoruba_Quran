package com.lucasurbas.masterdetail.ui.chapters;

import com.lucasurbas.masterdetail.data.Chapter;
import com.lucasurbas.masterdetail.ui.main.MainContract;

import javax.inject.Inject;

/**
 * Created by Lucas on 17/01/2017.
 */

public class ChapterNavigator implements ChapterContract.Navigator {

    private MainContract.Navigator mainNavigator;

    @Inject
    public ChapterNavigator(MainContract.Navigator mainNavigator) {
        this.mainNavigator = mainNavigator;
    }

    @Override
    public void goToPersonDetails(Chapter chapter) {
        mainNavigator.goToPersonDetails(chapter);
    }
}
