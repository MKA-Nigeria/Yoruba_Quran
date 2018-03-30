package com.alium.quran_app_data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;

import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_data.model.DataLoadState;
import com.alium.quran_app_data.paging.ChaptersListBoundaryCallback;
import com.alium.quran_app_data.repository.contracts.IChaptersContracts;
import com.alium.quran_app_data.utils.AppsExecutor;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by aliumujib on 25/03/2018.
 */

public class ChapterRepository implements IChaptersContracts.IChaptersRepository {
    private IChaptersContracts.IChaptersCache chaptersCache;
    private IChaptersContracts.IChaptersCloudDataBase chaptersCloudDataBase;
    private PagedList.Config config;
    private ChaptersListBoundaryCallback postsListBoundaryCallback;
    private MutableLiveData<DataLoadState> networkState;
    private LiveData chapterData;

    public ChapterRepository(IChaptersContracts.IChaptersCache chaptersCache, IChaptersContracts.IChaptersCloudDataBase chaptersCloudDataBase) {
        this.chaptersCache = chaptersCache;
        this.chaptersCloudDataBase = chaptersCloudDataBase;
        this.config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(150)
                .setPageSize(10)
                .build();
    }

    @Override

    public Flowable<List<Chapter>> getAllChapters() {

        return chaptersCache.getAllChaptersFromCache().flatMap(new Function<List<Chapter>, Publisher<List<Chapter>>>() {
            @Override
            public Publisher<List<Chapter>> apply(List<Chapter> chapters) throws Exception {
                if(chapters.isEmpty()){
                    return chaptersCloudDataBase.getAllChaptersFromCloud();
                }else {
                    return Flowable.fromArray(chapters);
                }
            }
        });
    }


    public MutableLiveData<DataLoadState> getNetworkState() {
        if (networkState == null) {
            networkState = new MutableLiveData<DataLoadState>();
        }
        return networkState;
    }

    @Override
    public void deleteAllChapters() {
        AppsExecutor.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                chaptersCache.deleteAllChapters();
            }
        });
    }
}
