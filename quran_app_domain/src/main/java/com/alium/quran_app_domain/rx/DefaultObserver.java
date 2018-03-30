package com.alium.quran_app_domain.rx;

import io.reactivex.observers.DisposableObserver;

/**
 * Default {@link DisposableObserver} base class to be used whenever you want default error handling.
 */
public class DefaultObserver<T> extends DisposableObserver<T> {
  @Override
  public void onNext(T t) {
    // Intentionally empty.
  }

  @Override
  public void onComplete() {
    // Intentionally empty.
  }

  @Override
  public void onError(Throwable exception) {
    // Intentionally empty.
    exception.printStackTrace();
  }
}