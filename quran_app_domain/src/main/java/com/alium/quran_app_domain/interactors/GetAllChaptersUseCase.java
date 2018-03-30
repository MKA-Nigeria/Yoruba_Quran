package com.alium.quran_app_domain.interactors;

import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_data.repository.contracts.IChaptersContracts;
import com.alium.quran_app_domain.base.BaseRxUseCase;
import com.alium.quran_app_domain.base.Params;
import com.alium.quran_app_domain.entities.ChapterEntity;
import com.alium.quran_app_domain.mapper.EntityMapper;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by aliumujib on 25/03/2018.
 */

public class GetAllChaptersUseCase extends BaseRxUseCase {

    IChaptersContracts.IChaptersRepository chaptersRepository;

    public GetAllChaptersUseCase(IChaptersContracts.IChaptersRepository chaptersRepository) {
        this.chaptersRepository = chaptersRepository;
    }

    @SuppressWarnings("unchecked")
    private Flowable<List<ChapterEntity>> getChapters() {
        return this.chaptersRepository.getAllChapters().flatMap(new Function<List<Chapter>, Publisher<List<ChapterEntity>>>() {
            @Override
            public Publisher<List<ChapterEntity>> apply(List<Chapter> chapters) throws Exception {
                return Flowable.fromArray(EntityMapper.mapFrom(chapters));
            }
        });
    }

    @Override
    protected Observable getObservable(Params params) {
        return getChapters().toObservable();
    }
}
