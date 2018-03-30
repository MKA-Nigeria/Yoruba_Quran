package com.alium.yoruba_quran.injection.data;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.alium.quran_app_data.Constants;
import com.alium.quran_app_data.persistence.QuranDatabase;
import com.alium.quran_app_data.persistence.dao.QuranDao;
import com.alium.yoruba_quran.injection.FragmentScope;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aliumujib on 29/03/2018.
 */

@Module
public class RoomModule {

    private QuranDatabase quranDatabase;

    public RoomModule(Context application) {
        if (quranDatabase == null) {
            this.quranDatabase = Room.databaseBuilder(application, QuranDatabase.class, Constants.DATABASE_NAME).build();
        }
    }


    @Provides
    public QuranDao providesPostsDao() {
        return this.quranDatabase.postsDao();
    }


    @Provides
    public QuranDatabase providesWordPressDatabase() {
        return quranDatabase;
    }


}
