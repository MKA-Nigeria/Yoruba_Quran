package com.lucasurbas.masterdetail.injection.people;

import com.lucasurbas.masterdetail.injection.FragmentScope;
import com.lucasurbas.masterdetail.ui.chapters.ChaptersFragment;

import dagger.Subcomponent;

/**
 * Created by Lucas on 04/01/2017.
 */
@FragmentScope
@Subcomponent(
        modules = {PeopleModule.class}
)
public interface PeopleComponent {

    void inject(ChaptersFragment fragment);
}
