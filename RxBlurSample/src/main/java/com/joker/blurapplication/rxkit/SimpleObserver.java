package com.joker.blurapplication.rxkit;

import rx.Observer;

public class SimpleObserver<T> implements Observer<T> {
  @Override public void onCompleted() {
  }

  @Override public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override public void onNext(T o) {

  }
}
