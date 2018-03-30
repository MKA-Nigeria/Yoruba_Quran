package com.alium.yoruba_quran.app;

import android.app.Application;
import android.content.Context;

import com.alium.quran_app_data.repository.ParseInitializer;
import com.alium.yoruba_quran.R;
import com.alium.yoruba_quran.injection.app.ApplicationComponent;
import com.alium.yoruba_quran.injection.app.ApplicationModule;
import com.alium.yoruba_quran.injection.app.DaggerApplicationComponent;
import com.alium.yoruba_quran.ui.util.FileUtils;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by aliumujib on 02/12/2017.
 */

public class QuranApplication extends Application {

    private static Context mInstance;
    private ApplicationComponent applicationComponent;
    private static FileUtils mFileUtilsInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mInstance = getApplicationContext();
        mFileUtilsInstance = new FileUtils();

        ParseInitializer.initPArse(mInstance, mInstance.getString(R.string.parse_app_id),
                mInstance.getString(R.string.client_key), mInstance.getString(R.string.server_url));
    }

    public static FileUtils getmFileUtilsInstance() {
        return mFileUtilsInstance;
    }

    public static ApplicationComponent getAppComponent(Context context) {
        return ((QuranApplication) context.getApplicationContext()).applicationComponent;
    }

}
