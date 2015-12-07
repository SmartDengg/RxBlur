package com.joker.blurapplication.rx.subscribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.joker.blurapplication.other.ExecutorUtil;
import com.joker.blurapplication.other.transformation.GlideTransformation;
import com.joker.blurapplication.other.IntegerVersionSignature;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by Joker on 2015/11/27.
 */
public class GlideBlurOnSubscribe implements Observable.OnSubscribe<GlideDrawable> {

  private static final String TAG = GlideBlurOnSubscribe.class.getSimpleName();
  private Context context;
  private int resId;
  private int radius;
  private Executor cacheExecutor;

  public GlideBlurOnSubscribe(@NonNull Context context, @DrawableRes int resId, int radius) {
    this.context = context;
    this.resId = resId;
    this.radius = radius;
    this.cacheExecutor = ExecutorUtil.getSingleThreadExecutor();
  }

  @Override public void call(final Subscriber<? super GlideDrawable> subscriber) {
    Preconditions.checkUiThread();

    if (!subscriber.isUnsubscribed()) {
      FutureTask<Bitmap> futureTask =
          new FutureTask<>(GlideBlurOnSubscribe.this.futureRunnable(subscriber), null);
      cacheExecutor.execute(futureTask);
      subscriber.add(Subscriptions.from(futureTask));
    }

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {

        Glide.get(context).getBitmapPool().clearMemory();
        GlideBlurOnSubscribe.this.context = null;
      }
    });
  }

  private Runnable futureRunnable(final Subscriber<? super GlideDrawable> subscriber) {

    return new Runnable() {
      @Override public void run() {

        if (!subscriber.isUnsubscribed()) {

          try {
            GlideDrawable glideDrawable = Glide.with(context)
                .load(resId)
                .signature(new IntegerVersionSignature(
                    System.identityHashCode(System.currentTimeMillis())))
                .bitmapTransform(
                    new GlideTransformation(context, Glide.get(context).getBitmapPool(), radius))
                .into(-1, -1)
                .get();

            subscriber.onNext(glideDrawable);
          } catch (InterruptedException | ExecutionException e) {
            subscriber.onError(e);
          }
        }
      }
    };
  }
}
