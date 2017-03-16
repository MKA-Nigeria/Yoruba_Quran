package com.lucasurbas.masterdetail.ui.util;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lucasurbas.masterdetail.app.Constants;
import com.lucasurbas.masterdetail.app.MasterDetailApplication;
import com.lucasurbas.masterdetail.data.Chapter;
import com.lucasurbas.masterdetail.injection.app.ApplicationComponent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static com.lucasurbas.masterdetail.app.Constants.FOLDER_NAME;

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
