package com.alium.yoruba_quran.injection.main;

import android.content.Context;

import com.alium.yoruba_quran.injection.ActivityScope;
import com.alium.yoruba_quran.ui.main.MainActivity;
import com.alium.yoruba_quran.ui.main.MainContract;
import com.alium.yoruba_quran.ui.main.MainNavigator;
import com.alium.yoruba_quran.ui.main.MainPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lucas on 12/06/16.
 */
@Module
public class MainModule {

    private final MainActivity mainActivity;

    public MainModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    @ActivityScope
    MainContract.Navigator provideMainNavigation(MainNavigator navigation) {
        return navigation;
    }

    @Provides
    @ActivityScope
    MainContract.Presenter provideMainPresenter(MainPresenter presenter) {
        return presenter;
    }

    @Provides
    @ActivityScope
    Context provideContext(){
        return mainActivity;
    }

    @Provides
    @ActivityScope
    MainActivity provideMainActivity(){
        return mainActivity;
    }
}
