package com.alium.yoruba_quran.injection.app;

import android.app.Application;

import com.alium.yoruba_quran.injection.data.DataModule;
import com.alium.yoruba_quran.injection.data.RoomModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by aliumujib on 19/06/16.
 */

@Singleton
@Component(
        modules = {ApplicationModule.class}
)
public interface ApplicationComponent {

    Application getApplication();

}
