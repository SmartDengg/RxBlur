package com.joker.blurapplication.rx.rxviewstub;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.ViewStub;
import com.joker.blurapplication.rx.subscribe.ViewStubEventOnSubscribe;
import rx.Observable;

/**
 * Created by Joker on 2015/11/30.
 */
public class RxViewStub {

  @CheckResult @NonNull
  public static Observable<ViewStubEvent> inflateEvents(@NonNull ViewStub viewStub) {
    return Observable.create(new ViewStubEventOnSubscribe(viewStub));
  }
}
