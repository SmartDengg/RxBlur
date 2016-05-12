package com.joker.blurapplication.rxkit.subscribe;

import android.view.View;
import android.view.ViewStub;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.joker.blurapplication.rxkit.rxviewstub.ViewStubEvent;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Joker on 2015/11/30.
 */
public class ViewStubEventOnSubscribe implements Observable.OnSubscribe<ViewStubEvent> {

    private ViewStub viewStub;

    public ViewStubEventOnSubscribe(ViewStub viewStub) {
        this.viewStub = viewStub;
    }

    @Override
    public void call(final Subscriber<? super ViewStubEvent> subscriber) {

        Preconditions.checkUiThread();

        ViewStub.OnInflateListener inflateListener = new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(ViewStubEvent.create(stub, inflated));
                }
            }
        };
        viewStub.setOnInflateListener(inflateListener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                viewStub.setOnInflateListener(null);
            }
        });
    }
}
