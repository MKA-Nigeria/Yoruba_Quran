package com.lucasurbas.masterdetail.ui.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by abdulmujibaliu on 3/14/17.
 *
 * DEAR DEV, AS TEMPTING AS WRTITNG YOUR OWN ZIP EXTRATOR IS, DON'T BOTHER TO DO IT, I DID, IT WAS SLOW,
 * UNLESS YOU HAVE TIME SHA... GOOD LUCK AND GODSPEED ;)
 */
public class Decompress extends AsyncTask<String, Void, Void> {
    private String _zipFile;
    private String _location;

    /*public Decompress(String zipFile, String location) {
        _zipFile = zipFile;
        _location = location;

        _dirChecker("");
    }*/

    public void unzip() {
        try  {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if(ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch(Exception e) {
            Log.e("Decompress", "unzip", e);
        }

    }

    private void _dirChecker(String dir) {
        File f = new File(_location + dir);

        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }


    @Override
    protected Void doInBackground(String... params) {
        _zipFile = params[0];
        _location = params[1];

        _dirChecker("");
        unzip();

        return null;
    }
}
