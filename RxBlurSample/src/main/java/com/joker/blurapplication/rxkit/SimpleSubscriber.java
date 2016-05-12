package com.joker.blurapplication.rxkit;

import rx.Subscriber;

/**
 * Created by Joker on 2015/11/27.
 */
public class SimpleSubscriber<T> extends Subscriber<T> {

  @Override public void onCompleted() {

  }

  @Override public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override public void onNext(T t) {

  }
}
