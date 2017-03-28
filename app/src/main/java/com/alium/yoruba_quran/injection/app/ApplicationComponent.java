package com.alium.yoruba_quran.injection.app;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lucas on 19/06/16.
 */

@Singleton
@Component(
        modules = {ApplicationModule.class}
)
public interface ApplicationComponent {

    Application getApplication();

}
