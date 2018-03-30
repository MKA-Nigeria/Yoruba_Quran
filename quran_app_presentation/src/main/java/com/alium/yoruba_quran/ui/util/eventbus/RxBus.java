package com.alium.yoruba_quran.ui.util.eventbus;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by aliumujib on 14/03/2018.
 */

public class RxBus {
    private static final RxBus sBus = new RxBus();

    private final PublishSubject<Object> mBus = PublishSubject.create();

    private RxBus() {

    }

    public static RxBus getInstance() {
        return sBus;
    }

    public void send(Object o) {
        mBus.onNext(o);
    }

    public Observable<Object> observe() {
        return mBus;
    }

    @SuppressWarnings("unchecked")
    public <T> Observable<T> observe(final Class<T> c) {
        return mBus.filter(new Predicate<Object>() {
            @Override
            public boolean test(Object o) throws Exception {
                return c.isAssignableFrom(o.getClass());
            }
        }).map(new Function<Object, T>() {
            @Override
            public T apply(Object o) throws Exception {
                return (T) o;
            }
        });

        //return mBus.filter(o -> c.isAssignableFrom(o.getClass())).map(o -> (T) o);
    }
}
