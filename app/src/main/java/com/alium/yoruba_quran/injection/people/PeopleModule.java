package com.alium.yoruba_quran.injection.people;

import com.alium.yoruba_quran.injection.FragmentScope;
import com.alium.yoruba_quran.ui.chapters.ChapterContract;
import com.alium.yoruba_quran.ui.chapters.ChapterNavigator;
import com.alium.yoruba_quran.ui.chapters.ChaptersPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lucas on 04/01/2017.
 */
@Module
public class PeopleModule {

    @Provides
    ChapterContract.Navigator providePeopleNavigator(ChapterNavigator navigator) {
        return navigator;
    }

    @Provides
    @FragmentScope
    ChapterContract.Presenter providePeoplePresenter(ChaptersPresenter presenter) {
        return presenter;
    }
}
