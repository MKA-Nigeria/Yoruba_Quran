package com.alium.quran_app_data.repository.contracts;


import io.reactivex.Observable;

/**
 * Created by aliumujib on 29/03/2018.
 */

public interface IFilepathRepository {

     Observable<String> getQuranDownloadPath();

}
