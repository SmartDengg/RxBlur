package com.joker.blurapplication.rx;

import rx.Subscriber;

/**
 * Created by Administrator on 2015/11/27.
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
