package com.alium.yoruba_quran.injection.main;

import com.alium.yoruba_quran.injection.app.ApplicationComponent;
import com.alium.yoruba_quran.injection.people.PeopleComponent;
import com.alium.yoruba_quran.injection.ActivityScope;
import com.alium.yoruba_quran.injection.people.PeopleModule;
import com.alium.yoruba_quran.ui.main.MainActivity;

import dagger.Component;

/**
 * Created by Lucas on 12/06/16.
 */
@ActivityScope
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {MainModule.class}
)
public interface MainComponent {

    void inject(MainActivity activity);

    PeopleComponent plus(PeopleModule peopleModule);

}
