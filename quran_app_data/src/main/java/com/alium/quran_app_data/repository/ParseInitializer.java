package com.alium.quran_app_data.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alium.quran_app_data.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

/**
 * Created by aliumujib on 29/03/2018.
 */

public class ParseInitializer {


    public static void initPArse(final Context context, String appId, String clientKey, String serverUrl) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(appId)
                .clientKey(clientKey)
                .server(serverUrl)
                .enableLocalDataStore()
                .build()
        );
        Parse.setLogLevel(Log.VERBOSE);


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(context, "Success saving install", Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
