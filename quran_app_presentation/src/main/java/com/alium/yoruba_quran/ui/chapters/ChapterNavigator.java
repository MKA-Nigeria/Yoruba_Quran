package com.alium.yoruba_quran.ui.chapters;


import com.alium.quran_app_domain.entities.ChapterEntity;
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
    public void goToPersonDetails(ChapterEntity chapter) {
        mainNavigator.goToPersonDetails(chapter);
    }
}
