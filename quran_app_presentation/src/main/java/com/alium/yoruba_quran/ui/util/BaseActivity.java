package com.alium.yoruba_quran.ui.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alium.yoruba_quran.injection.app.ApplicationComponent;
import com.alium.yoruba_quran.app.QuranApplication;

/**
 * Created by Lucas on 19/06/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

   public FileUtils fileUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent(QuranApplication.getAppComponent(this));

        fileUtils = QuranApplication.getmFileUtilsInstance();
    }



    protected abstract void setupActivityComponent(ApplicationComponent applicationComponent);
}
