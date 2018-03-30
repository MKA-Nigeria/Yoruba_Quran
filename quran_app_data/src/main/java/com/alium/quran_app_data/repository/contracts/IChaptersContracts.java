package com.alium.quran_app_data.repository.contracts;


import com.alium.quran_app_data.model.Chapter;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by aliumujib on 25/03/2018.
 */

public interface IChaptersContracts {

    interface IChaptersCloudDataBase {

        Publisher<List<Chapter>> getAllChaptersFromCloud();

    }

    interface IChaptersCache {

        Flowable<List<Chapter>> getAllChaptersFromCache();

        List<Long> saveChaptersToCache(List<Chapter> chapters);

        void deleteAllChapters();


    }


    interface IChaptersRepository {

        Flowable<List<Chapter>> getAllChapters();

        void deleteAllChapters();
    }


}
