package com.alium.yoruba_quran.ui.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alium.yoruba_quran.injection.app.ApplicationComponent;
import com.alium.yoruba_quran.app.MasterDetailApplication;

/**
 * Created by Lucas on 19/06/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

   public FileUtils mFileUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent(MasterDetailApplication.getAppComponent(this));

        mFileUtils = MasterDetailApplication.getmFileUtilsInstance();
    }



    protected abstract void setupActivityComponent(ApplicationComponent applicationComponent);
}
