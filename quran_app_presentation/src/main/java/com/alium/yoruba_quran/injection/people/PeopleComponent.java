package com.alium.yoruba_quran.injection.people;

import com.alium.yoruba_quran.injection.FragmentScope;
import com.alium.yoruba_quran.injection.data.DataModule;
import com.alium.yoruba_quran.injection.data.RoomModule;
import com.alium.yoruba_quran.ui.chapters.ChaptersFragment;

import dagger.Subcomponent;

/**
 * Created by aliumujib on 04/01/2017.
 */
@FragmentScope
@Subcomponent(
        modules = {PeopleModule.class, DataModule.class}
)
public interface PeopleComponent {

    void inject(ChaptersFragment fragment);

}
