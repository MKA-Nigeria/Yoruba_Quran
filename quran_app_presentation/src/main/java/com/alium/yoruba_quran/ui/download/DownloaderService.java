package com.alium.yoruba_quran.ui.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alium.yoruba_quran.R;
import com.alium.yoruba_quran.ui.PresentationConstants;
import com.alium.yoruba_quran.ui.util.BaseActivity;
import com.alium.yoruba_quran.ui.util.eventbus.RxBus;
import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.Func;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DownloaderService extends Service {

    private Fetch mFetch;
    private HashMap<Long, Integer> mDownloadMan = new HashMap<>();
    private HashMap<Long, Notification> mNotificationMan = new HashMap<>();
    private String fileURL;
    final String filePath = Environment.getExternalStorageDirectory() + PresentationConstants.TEMP_FOLDER_NAME + "/" + "YORUBA_QURAN.zip";
    final String folderPath = Environment.getExternalStorageDirectory().getPath() + PresentationConstants.TEMP_FOLDER_NAME;

    public DownloaderService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        try {
//
//        } catch (SQLiteConstraintException e) {
//            //ONCE UPON A TIME, SOMETHING WENT TERRIBLY WRONG, THE end
//            e.printStackTrace();
//            if (mFetch != null) {
//                mFetch.deleteAll();
//                mFetch.close();
//            }
//            deleteFile(filePath);
//        }

        if (intent == null) {
            stopForeground(true);
        }

        mFetch = new Fetch.Builder(getApplicationContext(), "Yoruba_Quran")
                .setDownloadConcurrentLimit(4) // Allows Fetch to download 4 downloads in Parallel.
                .enableLogging(true)
                .build();


        // request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG");

        if ((fileURL = intent.getExtras().getString(PresentationConstants.FILE_URL_EXTRA)) != null) {
            final Request request = new Request(fileURL, filePath);
            request.setPriority(Priority.HIGH);

            request.setNetworkType(NetworkType.ALL);

            //Delete all previous downloads ... whats the point of keeping them around
            mFetch.deleteAll();

            mFetch.enqueue(request, new Func<Download>() {
                        @Override
                        public void call(Download download) {
                            //Request successfully Queued for download
                            showDownloadNotification("Downloading Quran files");
                        }
                    }, new Func<Error>() {
                        @Override
                        public void call(Error error) {
                            mFetch.deleteAll();
                            mFetch.close();
                            RxBus.getInstance().send(DownloadState.FAILED(error.name()));
                            deleteFile(filePath);
                            Toast.makeText(getApplicationContext(), "Error downloading Quran files", Toast.LENGTH_LONG).show();
                        }
                    }
            );

            //TODO USE RXBus to send events to the UI
            mFetch.addListener(new AbstractFetchListener() {
                @Override
                public void onCompleted(Download download) {
                    super.onCompleted(download);
                    stopForeground(true);
                    RxBus.getInstance().send(DownloadState.SUCCESS());
                    Toast.makeText(getApplicationContext(), "Quran files downloaded", Toast.LENGTH_LONG).show();
                    new UnzipAysncTask().execute(filePath, folderPath);
                   // mFetch.delete(download.getId());
                    mFetch.close();
                }

                @Override
                public void onProgress(Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
                    super.onProgress(download, etaInMilliSeconds, downloadedBytesPerSecond);
                    upDateNotification(download.getProgress(), "Downloading Quran files");
                }

                @Override
                public void onError(Download download) {
                    super.onError(download);
                    mFetch.delete(download.getId());
                    mFetch.close();
                    deleteFile(filePath);
                    stopForeground(true);
                    RxBus.getInstance().send(DownloadState.FAILED(download.getError().name()));
                    Toast.makeText(getApplicationContext(), "Error downloading Quran files, Please retry", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPaused(Download download) {
                    super.onPaused(download);
                }

                @Override
                public void onResumed(Download download) {
                    super.onResumed(download);
                }
            });
        } else {
            RxBus.getInstance().send(DownloadState.FAILED("Error downloading Quran files"));
            Toast.makeText(getApplicationContext(), "Error downloading Quran files", Toast.LENGTH_LONG).show();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    static class UnzipAysncTask extends AsyncTask<String, Void, Void> {
        String source = "";
        String destination = "";
        String password = "password";

        public void unzip() {
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
            destination = params[1];

            unzip();
            return null;
        }
    }

    public void showDownloadNotification(String title) {
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
        startForeground(200, notification);
    }


    private void upDateNotification(int progress, String title) {
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

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(200, notification);
    }

    public boolean deleteFile(String path) {
        File file = new File(path);
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
                if (file.exists()) {
                    getApplicationContext().deleteFile(file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFetch.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
