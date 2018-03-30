package com.alium.quran_app_data.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_data.persistence.dao.QuranDao;

/**
 * Created by aliumujib on 12/03/2018.
 */


@Database(entities = {Chapter.class}, version = 1, exportSchema = false)
public abstract class QuranDatabase extends RoomDatabase {

    public abstract QuranDao postsDao();

}
