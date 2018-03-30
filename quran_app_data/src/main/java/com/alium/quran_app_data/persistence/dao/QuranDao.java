package com.alium.quran_app_data.persistence.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.alium.quran_app_data.model.Chapter;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by aliumujib on 12/03/2018.
 */

@Dao
public interface QuranDao {

    @Query("SELECT * FROM Chapter ORDER BY indexID ASC")
    Flowable<List<Chapter>> getChaptersList();

    @Query("DELETE FROM Chapter")
    void deleteAllChapters();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveChapters(List<Chapter> postEntity);
}
