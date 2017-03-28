package com.alium.yoruba_quran.ui.chapters;

import com.alium.yoruba_quran.data.Chapter;
import com.alium.yoruba_quran.ui.main.MainContract;

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
