package com.alium.yoruba_quran.injection.people;

import com.alium.yoruba_quran.injection.FragmentScope;
import com.alium.yoruba_quran.ui.chapters.ChaptersFragment;

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
