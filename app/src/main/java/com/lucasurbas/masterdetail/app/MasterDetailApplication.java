package com.lucasurbas.masterdetail.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.lucasurbas.masterdetail.injection.app.ApplicationComponent;
import com.lucasurbas.masterdetail.injection.app.ApplicationModule;
import com.lucasurbas.masterdetail.injection.app.DaggerApplicationComponent;
import com.lucasurbas.masterdetail.ui.util.FileUtils;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import fr.xebia.android.freezer.Freezer;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Lucas on 02/01/2017.
 */

public class MasterDetailApplication extends Application {

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
        Freezer.onCreate(this);
        reInitPArse(mInstance);
    }

    public static FileUtils getmFileUtilsInstance() {
        return mFileUtilsInstance;
    }

    public static ApplicationComponent getAppComponent(Context context) {
        return ((MasterDetailApplication) context.getApplicationContext()).applicationComponent;
    }

    private static void reInitPArse(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId("y1yQakWPdacm0IM7w0Npd8rywmV3UvISAmLdUkKu")
                .clientKey("8QHESWaH5MZ6IXfmuVsWlOYOPgbR0RfA6NdFQKNG")
                .server("https://parseapi.back4app.com/").enableLocalDataStore()
                .build()
        );
        Parse.setLogLevel(Log.VERBOSE);


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(mInstance, "Success saving install", Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                    Toast.makeText(mInstance, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
