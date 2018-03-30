package com.alium.quran_app_data.repository;

import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Environment;

import com.alium.quran_app_data.Constants;
import com.alium.quran_app_data.repository.contracts.IFilepathRepository;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by aliumujib on 29/03/2018.
 */

public class FilepathRepository implements IFilepathRepository {

    @Override
    public Observable<String> getQuranDownloadPath() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> source) throws Exception {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.FILES_TABLE_NAME);
                query.setLimit(200);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            String string = object.getString(Constants.FILE_URL_STRING);
                            if (object.getString(Constants.FILE_URL_STRING) != null) {
                                source.onNext(string);
                            }
                        } else {
                            source.onError(e);
                        }
                    }
                });
            }
        });
    }
}
