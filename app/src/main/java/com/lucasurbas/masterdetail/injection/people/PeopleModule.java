package com.lucasurbas.masterdetail.injection.people;

import com.lucasurbas.masterdetail.injection.FragmentScope;
import com.lucasurbas.masterdetail.ui.chapters.ChapterContract;
import com.lucasurbas.masterdetail.ui.chapters.ChapterNavigator;
import com.lucasurbas.masterdetail.ui.chapters.ChaptersPresenter;

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
