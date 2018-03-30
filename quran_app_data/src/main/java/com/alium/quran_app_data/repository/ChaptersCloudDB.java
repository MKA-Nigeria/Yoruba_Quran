package com.alium.quran_app_data.repository;

import android.util.Log;

import com.alium.quran_app_data.Constants;
import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_data.repository.contracts.IChaptersContracts;
import com.alium.quran_app_data.utils.AppsExecutor;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by aliumujib on 25/03/2018.
 */

public class ChaptersCloudDB implements IChaptersContracts.IChaptersCloudDataBase {


    IChaptersContracts.IChaptersCache chaptersCache;

    public ChaptersCloudDB(IChaptersContracts.IChaptersCache chaptersCache) {
        this.chaptersCache = chaptersCache;
    }

    @Override
    public Flowable<List<Chapter>> getAllChaptersFromCloud() {
        return Flowable.create(new FlowableOnSubscribe<List<Chapter>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Chapter>> source) throws Exception {
                final List<Chapter> chapters = new ArrayList<>();
                ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.CHAPTER_CHAPTERS_TABLE_NAME);
                query.setLimit(200);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            Log.d("CHAPTER", "OBJECT SIZE" + objects.size());
                            for (int i = 0; i < objects.size(); i++) {
                                ParseObject object = objects.get(i);
                                Chapter chapter = new Chapter(object.getObjectId());
                                chapter.setName(object.getString(Constants.CHAPTER_TITLE));
                                String desc;

                                if (object.getBoolean(Constants.CHAPTER_IS_MECCAN)) {
                                    desc = "Mecca";
                                } else {
                                    desc = "Medina";
                                }

                                chapter.setDescription(desc);
                                chapter.setIndexID(object.getInt(Constants.CHAPTER_INDEX_ID));
                                chapter.setVerseCount(object.getInt(Constants.CHAPTER_VERSE_COUNT));

                                chapters.add(chapter);
                            }
                            //source.onNext(chapters);
                            AppsExecutor.networkIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    chaptersCache.saveChaptersToCache(chapters);
                                }
                            });
                            //getPeople();
                        } else {
                            source.onError(e);
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, BackpressureStrategy.BUFFER);

    }
}
