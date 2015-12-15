package com.joker.blurapplication.rx.subscribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.joker.blurapplication.other.util.ExecutorUtil;
import com.joker.blurapplication.other.util.FastBlurUtil;
import com.joker.blurapplication.other.TargetAdapter;
import com.joker.blurapplication.rx.PicassoError;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Joker on 2015/11/27.
 */
public class FastBlurOnSubscribe implements Observable.OnSubscribe<Bitmap> {

  private static final String TAG = FastBlurOnSubscribe.class.getSimpleName();
  private Context context;
  private int resId;
  private float scale = 1L;
  private int radius;
  private Executor ioExecutor;

  public FastBlurOnSubscribe(@NonNull Context context, @DrawableRes int resId, int radius) {
    this.context = context;
    this.resId = resId;
    this.radius = radius;
    this.ioExecutor = ExecutorUtil.getIoExecutor();
  }

  @Override public void call(final Subscriber<? super Bitmap> subscriber) {

    Preconditions.checkUiThread();

    final Target target = new TargetAdapter() {
      @Override public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

        if (!subscriber.isUnsubscribed()) {
          FutureTask<Bitmap> futureTask =
              new FutureTask<>(FastBlurOnSubscribe.this.futureRunnable(bitmap, subscriber), null);
          ioExecutor.execute(futureTask);
          subscriber.add(Subscriptions.from(futureTask));
        }
      }

      @Override public void onBitmapFailed(Drawable errorDrawable) {
        if (!subscriber.isUnsubscribed()) subscriber.onError(new PicassoError(errorDrawable));
      }
    };

    Picasso
        .with(context)
        .load(resId)
        .noFade()
        .config(Bitmap.Config.ARGB_8888)
        .memoryPolicy(NO_CACHE, NO_STORE)
        .into(target);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        Picasso.with(context).cancelRequest(target);
        FastBlurOnSubscribe.this.context = null;
      }
    });
  }

  private Runnable futureRunnable(final Bitmap sourceBitmap, final Subscriber<? super Bitmap> subscriber) {

    return new Runnable() {
      @Override public void run() {

        if (!subscriber.isUnsubscribed()) {

          Bitmap blurBitmap = FastBlurUtil.blur(sourceBitmap, scale, radius);
          subscriber.onNext(blurBitmap);
        }
      }
    };
  }
}
