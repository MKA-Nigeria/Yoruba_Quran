package com.alium.yoruba_quran.ui.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alium.yoruba_quran.R;
import com.alium.yoruba_quran.app.Constants;
import com.alium.yoruba_quran.data.Chapter;
import com.alium.yoruba_quran.data.Download;
import com.alium.yoruba_quran.data.DownloadEntityManager;
import com.alium.yoruba_quran.injection.app.ApplicationComponent;
import com.alium.yoruba_quran.injection.main.DaggerMainComponent;
import com.alium.yoruba_quran.injection.main.MainComponent;
import com.alium.yoruba_quran.injection.main.MainModule;
import com.alium.yoruba_quran.ui.main.MainContract;
import com.alium.yoruba_quran.ui.util.BaseActivity;
import com.alium.yoruba_quran.ui.warning_fragment.WarningFragment;
import com.alium.yoruba_quran.ui.widget.ContainersLayout;
import com.alium.yoruba_quran.ui.widget.CustomAppBar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;



public class MainActivity extends BaseActivity implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    @Inject MainContract.Presenter presenter;
    @Inject MainContract.Navigator navigator;

    @BindView(R.id.activity_main__nav) NavigationView navigationView;
    @Nullable
    NavigationView navigationSideView;
    @Nullable
    @BindView(R.id.activity_main__insets)
    ViewGroup insetsView;
    @BindView(R.id.activity_main__drawer) DrawerLayout drawer;
    @BindView(R.id.activity_main__custom_appbar)
    CustomAppBar customAppBar;
    @BindView(R.id.activity_main__containers_layout)
    ContainersLayout containersLayout;

    private MainComponent mainComponent;
    private ArrayList<Chapter> peopleList;
    private Fetch mFetch;
    private NotificationManager mNotificationManager;
    private HashMap<Long, Integer> mDownloadMan = new HashMap<>();
    private HashMap<Long, Notification> mNotificationMan = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFetch = Fetch.getInstance(this);


        mFetch.addFetchListener(new FetchListener() {
            @Override
            public void onUpdate(long l, int i, int i1, long l1, long l2, int i2) {
                if(i == Fetch.STATUS_DONE) {
                    cancelUploadNotifcation(mDownloadMan.get(l));
                    DownloadEntityManager downloadEntityManager = new DownloadEntityManager();
                    Download download = new Download();
                    download.setDownLoadDone(true);
                    download.setInDexID(mDownloadMan.get(l));
                    downloadEntityManager.add(download);
                    String fileURl = Environment.getExternalStorageDirectory() + Constants.TEMP_FOLDER_NAME+"/"+"YORUBA_QURAN.zip";
                    String folderPath = Environment.getExternalStorageDirectory().getPath();
                    new UnzipAysncTask().execute(fileURl, folderPath);
                }

                if(i == Fetch.STATUS_DOWNLOADING) {
                   // Log.d("DOWNLOADER: ")
                    upDateNotification(i1, mDownloadMan.get(l), "Downloading Quran File No: "+ mDownloadMan.get(l));
                }

                if(i== Fetch.STATUS_ERROR){
                    String fileURl = Environment.getExternalStorageDirectory() + Constants.TEMP_FOLDER_NAME+"/"+"YORUBA_QURAN.zip";
                    cancelUploadNotifcation(mDownloadMan.get(l));

                    mFetch.remove(l);
                    deleteFile(fileURl);
                }
            }
        });


        if (insetsView != null && navigationSideView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(insetsView, new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    ((ViewGroup.MarginLayoutParams) insetsView.getLayoutParams()).topMargin = insets.getSystemWindowInsetTop();
                    ((ViewGroup.MarginLayoutParams) insetsView.getLayoutParams()).bottomMargin = insets.getSystemWindowInsetBottom();
                    insetsView.requestLayout();
                    ((ViewGroup.MarginLayoutParams) navigationSideView.getLayoutParams()).topMargin = (-insets.getSystemWindowInsetTop());
                    navigationSideView.requestLayout();
                    return insets.consumeSystemWindowInsets();
                }
            });
            navigationSideView.setNavigationItemSelectedListener(this);
        }

        navigationView.setNavigationItemSelectedListener(this);
        customAppBar.setOnNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        presenter.attachView(this);
        if (savedInstanceState == null) {
            presenter.clickPeople();
        }

        //TODO if is first lauch ... Show getFile dialog
        getFiles();
        /*String fileURl = Environment.getExternalStorageDirectory() + TEMP_FOLDER_NAME+"/"+"YORUBA_QURAN.zip";
        String folderPath = Environment.getExternalStorageDirectory().getPath();
        //String fileNAme = object.getString(Constants.FILE_ACCESSOR_ID)+"YOR.PDF";

        new UnzipAysncTask().execute(fileURl, folderPath);*/

    }

    class UnzipAysncTask extends AsyncTask<String, Void,Void>{
        String source = "";
        String destination = "";
        String password = "password";

        public void unzip(){
            try {
                ZipFile zipFile = new ZipFile(source);
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password);
                }
                zipFile.extractAll(destination);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            source = params[0];
            destination=params[1];

            unzip();
            return null;
        }
    }


    public boolean deleteFile(String path){
        File file = new File(path);
        file.delete();
        if(file.exists()){
            try {
                file.getCanonicalFile().delete();
                if(file.exists()){
                    getApplicationContext().deleteFile(file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private void upDateNotification(int progress, int notificationID, String title){
        Intent intent = new Intent(this, BaseActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        final Notification notification = new Notification(R.drawable.ic_verse_bg, title, System
                .currentTimeMillis());
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.contentView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.progress_layout);
        notification.contentIntent = pendingIntent;
        notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_verse_bg);
        notification.contentView.setTextViewText(R.id.status_text, title);
        notification.contentView.setProgressBar(R.id.status_progress, 100, progress, false);
        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(
                getApplicationContext().NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, notification);
    }



    private void getFiles(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.FILES_TABLE_NAME);
        query.setLimit(200);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject object = objects.get(i);
                        if(object.getString(Constants.FILE_URL_STRING)!=null){
                            String fileURl = Environment.getExternalStorageDirectory() + Constants.TEMP_FOLDER_NAME+"/"+"YORUBA_QURAN.zip";
                            String folderPath = Environment.getExternalStorageDirectory().getPath() + Constants.TEMP_FOLDER_NAME;

                            String fileNAme = "YORUBA_QURAN.zip";
                            if(!mFileUtils.checkExistsFile(fileURl)){
                                android.app.FragmentManager manager = getFragmentManager();
                                WarningFragment fragment1 = WarningFragment.getInstance(MainActivity.this);
                                Bundle args = new Bundle();
                                fragment1.setArguments(args);
                                fragment1.show(manager, "");

                                Request request = new Request(object.getString(Constants.FILE_URL_STRING),folderPath,fileNAme);
                                long downloadId = mFetch.enqueue(request);
                                mDownloadMan.put(downloadId, object.getInt(Constants.FILE_ACCESSOR_ID));
                                showUploadNotification("Downloading Quran File No: "+ object.getInt(Constants.FILE_ACCESSOR_ID),
                                        (object.getInt(Constants.FILE_ACCESSOR_ID)), downloadId);

                                Toast.makeText(MainActivity.this, "File not exits, downloading", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MainActivity.this, "File exits", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    e.printStackTrace();
                    //TODO show error dialog
                }
            }
        });
    }


    public void cancelUploadNotifcation(int notificationID) {
        mNotificationManager.cancel(notificationID);
    }

    public void showUploadNotification(String title, int notificationID, long downloadId) {
        // configure the intent
        Intent intent = new Intent(this, BaseActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        final Notification notification = new Notification(R.drawable.ic_verse_bg, title, System
                .currentTimeMillis());
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.contentView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.progress_layout);
        notification.contentIntent = pendingIntent;
        notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_verse_bg);
        notification.contentView.setTextViewText(R.id.status_text, title);
        notification.contentView.setProgressBar(R.id.status_progress, 100, 0, true);
        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(
                getApplicationContext().NOTIFICATION_SERVICE);

        mNotificationMan.put(downloadId, notification);
        mNotificationManager.notify(notificationID, notification);
    }


    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        mainComponent = DaggerMainComponent.builder()
                .applicationComponent(applicationComponent)
                .mainModule(new MainModule(this))
                .build();

        mainComponent.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void closeDrawer() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }, 100);
        }
    }

    @Override
    public void openDrawer() {
        if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void highlightHomeFeed() {
        navigationView.setCheckedItem(R.id.menu_main_nav__settings);
    }

    public void toggleDrawer() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }


    @Override
    public void highlightPeople() {
        navigationView.setCheckedItem(R.id.menu_main_nav__people);
        if (navigationSideView != null) {
            navigationSideView.setCheckedItem(R.id.menu_main_nav__people);
        }
    }

    @Override
    public void highlightFavorites() {
        navigationView.setCheckedItem(R.id.menu_main_nav__favorites);
        if (navigationSideView != null) {
            navigationSideView.setCheckedItem(R.id.menu_main_nav__favorites);
        }
    }

    @Override
    public void highlightSettings() {
        navigationView.setCheckedItem(R.id.menu_main_nav__settings);
    }

    @Override
    public void highlightFeedback() {
        navigationView.setCheckedItem(R.id.menu_main_nav__feedback);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_nav__people:
                presenter.clickPeople();
                break;


            case R.id.menu_main_nav__favorites:
                presenter.clickFavorites();
                break;


            case R.id.menu_main_nav__settings:
                presenter.clickSettings();
                break;

            case R.id.menu_main_nav__feedback:
                presenter.clickFeedback();
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!navigator.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public CustomAppBar getCustomAppBar() {
        return customAppBar;
    }

    public ContainersLayout getContainersLayout() {
        return containersLayout;
    }

    public MainContract.Navigator getNavigator() {
        return navigator;
    }

    public MainComponent getMainComponent() {
        return mainComponent;
    }

}
