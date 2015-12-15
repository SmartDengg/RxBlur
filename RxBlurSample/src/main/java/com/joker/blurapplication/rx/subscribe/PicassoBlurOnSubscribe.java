package com.joker.blurapplication.rx.subscribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.joker.blurapplication.other.TargetAdapter;
import com.joker.blurapplication.other.transformation.PicassoTransformation;
import com.joker.blurapplication.rx.PicassoError;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import rx.Observable;
import rx.Subscriber;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Joker on 2015/11/27.
 */
public class PicassoBlurOnSubscribe implements Observable.OnSubscribe<Bitmap> {

  private static final String TAG = PicassoBlurOnSubscribe.class.getSimpleName();
  private Context context;
  private int resId;
  private int radius;

  public PicassoBlurOnSubscribe(@NonNull Context context, @DrawableRes int resId, int radius) {
    this.context = context;
    this.resId = resId;
    this.radius = radius;
  }

  @Override public void call(final Subscriber<? super Bitmap> subscriber) {

    Preconditions.checkUiThread();

    final Target target = new TargetAdapter() {
      @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (!subscriber.isUnsubscribed()) subscriber.onNext(bitmap);
      }

      @Override public void onBitmapFailed(Drawable errorDrawable) {
        if (!subscriber.isUnsubscribed()) subscriber.onError(new PicassoError(errorDrawable));
      }
    };

    Picasso
        .with(context)
        .load(resId)
        .noFade()
        .memoryPolicy(NO_CACHE, NO_STORE)
        .config(Bitmap.Config.ARGB_8888)
        .transform(new PicassoTransformation(context, radius))
        .into(target);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        Picasso.with(context).cancelRequest(target);
        PicassoBlurOnSubscribe.this.context = null;
      }
    });
  }
}
