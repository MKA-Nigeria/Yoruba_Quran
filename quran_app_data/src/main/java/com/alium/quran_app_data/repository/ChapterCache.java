package com.alium.quran_app_data.repository;

import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_data.persistence.dao.QuranDao;
import com.alium.quran_app_data.repository.contracts.IChaptersContracts;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * Created by aliumujib on 25/03/2018.
 */

public class ChapterCache implements IChaptersContracts.IChaptersCache {

    private final QuranDao quranDao;

    @Inject
    public ChapterCache(QuranDao quranDao) {
        this.quranDao = quranDao;
    }


    @Override
    public Flowable<List<Chapter>> getAllChaptersFromCache() {
        return quranDao.getChaptersList();
    }

    @Override
    public List<Long> saveChaptersToCache(List<Chapter> chapters) {
        return quranDao.saveChapters(chapters);
    }

    @Override
    public void deleteAllChapters() {
        quranDao.deleteAllChapters();
    }
}
