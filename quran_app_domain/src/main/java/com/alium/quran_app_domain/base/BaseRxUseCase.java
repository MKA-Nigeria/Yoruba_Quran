package com.alium.quran_app_domain.base;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Abstract class for GetAllChaptersUseCase UseCase/Interactor
 * Will execute its job in GetAllChaptersUseCase background thread and will post the result in the UI thread.
 * Will return the result using GetAllChaptersUseCase {@link Disposable}
 */
public abstract class BaseRxUseCase {

    private final CompositeDisposable disposables;

    public BaseRxUseCase() {
        this.disposables = new CompositeDisposable();
    }

    protected abstract Observable getObservable(Params params);

    /**
     * Executes the current UseCase.
     *
     * @param observer {@link DisposableObserver} which will be listening to the observable build
     * with {@link #getObservable(Params)}.
     */
    @SuppressWarnings("unchecked")
    public void execute(DisposableObserver observer, Params params) {
        final Observable<?> observable = this.getObservable(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        addDisposable(observable.subscribeWith(observer));
    }


    /**
     * Dispose from current {@link CompositeDisposable}.
     */
    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    /**
     * Dispose from current {@link CompositeDisposable}.
     */
    private void addDisposable(Disposable disposable) {
        if (disposable != null) {
            disposables.add(disposable);
        }
    }
}
