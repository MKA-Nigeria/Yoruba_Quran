package com.alium.yoruba_quran.injection.main;

import com.alium.yoruba_quran.injection.app.ApplicationComponent;
import com.alium.yoruba_quran.injection.data.DataModule;
import com.alium.yoruba_quran.injection.data.RoomModule;
import com.alium.yoruba_quran.injection.people.PeopleComponent;
import com.alium.yoruba_quran.injection.ActivityScope;
import com.alium.yoruba_quran.injection.people.PeopleModule;
import com.alium.yoruba_quran.ui.main.MainActivity;

import dagger.Component;

/**
 * Created by aliumujib on 12/06/16.
 */
@ActivityScope
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {MainModule.class}
)
public interface MainComponent {

    void inject(MainActivity activity);

    PeopleComponent plus(PeopleModule peopleModule, RoomModule roomModule);


}
