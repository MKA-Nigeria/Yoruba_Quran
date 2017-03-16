package com.lucasurbas.masterdetail.ui.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.lucasurbas.masterdetail.app.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static com.lucasurbas.masterdetail.app.Constants.FILE_TYPE;
import static com.lucasurbas.masterdetail.app.Constants.FOLDER_NAME;
import static com.lucasurbas.masterdetail.app.Constants.TEMP_FOLDER_NAME;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private File filesFolder;


    public FileUtils() {
        initFilesFolder();
    }

    private void initFilesFolder() {
        filesFolder = new File(Environment.getExternalStorageDirectory() + TEMP_FOLDER_NAME);
        if (!filesFolder.exists()) {
            filesFolder.mkdirs();
            initNoMediaFile(Environment.getExternalStorageDirectory() + TEMP_FOLDER_NAME, ".nomedia");
        }
    }

    private void initNoMediaFile(String path, String sFileName) {
        try {
            File root = new File(path);
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(path, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkExistsFile(String fileUrlString) {
        File file = new File(fileUrlString);
        if (!file.exists()) {
            return false;
        } else {
            return true;
        }
    }


    public File createFileIfNotExist(String fileUrlString) {
        Uri fileUri = Uri.parse(fileUrlString);
        String fileName = fileUri.getLastPathSegment() + FILE_TYPE;
        return new File(filesFolder, fileName);
    }

    private void saveFile(File file, Bitmap bitmap) {
        // starting new Async Task
        new SavingFileTask().execute(file, bitmap);
    }

    private class SavingFileTask extends AsyncTask<Object, String, Object> {

        @Override
        protected Object doInBackground(Object... objects) {
            File file = (File) objects[0];
            Bitmap bitmap = (Bitmap) objects[1];

            FileOutputStream fileOutputStream;

            try {
                fileOutputStream = new FileOutputStream(file);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, Constants.ZERO_INT_VALUE, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                fileOutputStream.write(byteArray);

                fileOutputStream.flush();
                fileOutputStream.close();

                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();

            } catch (FileNotFoundException e) {
                //ErrorUtils.logError(e);
            } catch (IOException e) {
                // ErrorUtils.logError(e);
            }

            return null;
        }
    }
}