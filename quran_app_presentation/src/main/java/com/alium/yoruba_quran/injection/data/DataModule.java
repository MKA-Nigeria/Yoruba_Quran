package com.alium.yoruba_quran.injection.data;

import com.alium.quran_app_data.persistence.dao.QuranDao;
import com.alium.quran_app_data.repository.ChapterCache;
import com.alium.quran_app_data.repository.ChapterRepository;
import com.alium.quran_app_data.repository.ChaptersCloudDB;
import com.alium.quran_app_data.repository.FilepathRepository;
import com.alium.quran_app_data.repository.contracts.IChaptersContracts;
import com.alium.quran_app_data.repository.contracts.IFilepathRepository;
import com.alium.yoruba_quran.injection.FragmentScope;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aliumujib on 29/03/2018.
 */

@Module(includes = {RoomModule.class})
public class DataModule {

    @Provides
    IChaptersContracts.IChaptersRepository getChaptersRepository(IChaptersContracts.IChaptersCache chaptersCache, IChaptersContracts.IChaptersCloudDataBase chaptersCloudDataBase) {
        return new ChapterRepository(chaptersCache, chaptersCloudDataBase);
    }


    @Provides
    IChaptersContracts.IChaptersCloudDataBase providesChaptersCloudDataBase(IChaptersContracts.IChaptersCache chaptersCache) {
        return new ChaptersCloudDB(chaptersCache);
    }

    @Provides
    IChaptersContracts.IChaptersCache providesIChaptersCache(QuranDao quranDao) {
        return new ChapterCache(quranDao);
    }


}
