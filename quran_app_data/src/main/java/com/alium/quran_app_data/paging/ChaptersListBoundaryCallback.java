package com.alium.quran_app_data.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_data.model.DataLoadState;
import com.alium.quran_app_data.repository.contracts.IChaptersContracts;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.functions.Consumer;

/**
 * Created by aliumujib on 21/03/2018.
 */

public class ChaptersListBoundaryCallback extends PagedList.BoundaryCallback<Chapter> {

    private IChaptersContracts.IChaptersCloudDataBase iChaptersCloudDataBase;
    private IChaptersContracts.IChaptersCache iChaptersCache;
    private PagingRequestHelper helper;
    private MutableLiveData<DataLoadState> networkState;


    public ChaptersListBoundaryCallback(IChaptersContracts.IChaptersCloudDataBase remotePostsDataStore, IChaptersContracts.IChaptersCache postsCache, Executor ioExecutor, MutableLiveData<DataLoadState> networkState) {
        this.iChaptersCloudDataBase = remotePostsDataStore;
        this.iChaptersCache = postsCache;
        this.helper = new PagingRequestHelper(ioExecutor);
        this.networkState = networkState;
        initNetworkStateLiveData(networkState);
    }

    public MutableLiveData<DataLoadState> getNetworkState() {
        return networkState;
    }

    private void initNetworkStateLiveData(final MutableLiveData<DataLoadState> loadStateMutableLiveData) {
        helper.addListener(new PagingRequestHelper.Listener() {
            @Override
            public void onStatusChange(@NonNull PagingRequestHelper.StatusReport report) {
                if (report.hasRunning()) {
                    loadStateMutableLiveData.postValue(DataLoadState.LOADING());
                } else if (report.hasError()) {
                    loadStateMutableLiveData.postValue(DataLoadState.FAILED(getError(report)));
                } else {
                    loadStateMutableLiveData.postValue(DataLoadState.SUCCESS());
                }
            }
        });
    }

    private String getError(PagingRequestHelper.StatusReport report) {
        if (report.after == PagingRequestHelper.Status.FAILED) {
            return report.getErrorFor(PagingRequestHelper.RequestType.AFTER).getMessage();
        } else if (report.before == PagingRequestHelper.Status.FAILED) {
            return report.getErrorFor(PagingRequestHelper.RequestType.BEFORE).getMessage();
        } else if (report.initial == PagingRequestHelper.Status.FAILED) {
            return report.getErrorFor(PagingRequestHelper.RequestType.INITIAL).getMessage();
        }

        return "Unknown error";
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL, new PagingRequestHelper.Request() {
            @Override
            public void run(Callback callback) {
                loadFromServer(callback);
            }
        });
    }

    private void loadFromServer(final PagingRequestHelper.Request.Callback callback) {
        //networkState.postValue(DataLoadState.LOADING());
//        iChaptersCloudDataBase.getAllChaptersFromCloud()
//                .subscribe(new Consumer<List<Chapter>>() {
//                    @Override
//                    public void accept(final List<Chapter> chapterList) throws Exception {
//                        //networkState.postValue(DataLoadState.SUCCESS());
//                        callback.recordSuccess();
//                        AsyncTask.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                iChaptersCache.saveChaptersToCache(chapterList);
//                            }
//                        });
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        callback.recordFailure(throwable);
//                        throwable.printStackTrace();
//                    }
//                });
    }

    @Override
    public void onItemAtFrontLoaded(@NonNull Chapter itemAtFront) {
        super.onItemAtFrontLoaded(itemAtFront);

        //loadFromServer(pageSize);
    }

    @Override
    public void onItemAtEndLoaded(@NonNull Chapter itemAtEnd) {
        super.onItemAtEndLoaded(itemAtEnd);
    }
}
