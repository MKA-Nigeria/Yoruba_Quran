package com.alium.quran_app_domain.interactors;

import com.alium.quran_app_data.repository.contracts.IFilepathRepository;
import com.alium.quran_app_domain.base.BaseRxUseCase;
import com.alium.quran_app_domain.base.Params;

import io.reactivex.Observable;

/**
 * Created by aliumujib on 29/03/2018.
 */

public class GetDownloadURLUseCase extends BaseRxUseCase {

    IFilepathRepository repository;

    public GetDownloadURLUseCase(IFilepathRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable getObservable(Params params) {
        return repository.getQuranDownloadPath();
    }
}
