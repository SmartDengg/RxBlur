package com.joker.blurapplication.rxkit.subscribe;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.joker.blurapplication.other.BlurEvaluator;
import com.joker.blurapplication.other.TargetAdapter;
import com.joker.blurapplication.rxkit.PicassoError;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import rx.Observable;
import rx.Subscriber;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Joker on 2015/11/27.
 */
public class AnimationBlurOnSubscribe implements Observable.OnSubscribe<Bitmap> {

  private static final String TAG = AnimationBlurOnSubscribe.class.getSimpleName();
  private Context context;
  private int resId;
  private int radius;
  private long duration;
  private ValueAnimator blurAnimator;

  public AnimationBlurOnSubscribe(@NonNull Context context, @DrawableRes int resId, int radius,
      long duration) {
    this.context = context;
    this.resId = resId;
    this.radius = radius;
    this.duration = duration;
  }

  @Override public void call(final Subscriber<? super Bitmap> subscriber) {

    Preconditions.checkUiThread();

    final Target target = new TargetAdapter() {
      @Override public void onBitmapLoaded(final Bitmap source, Picasso.LoadedFrom from) {
        if (!subscriber.isUnsubscribed()) {

          blurAnimator = ValueAnimator.ofInt(1, radius);
          blurAnimator.setEvaluator(new BlurEvaluator());
          blurAnimator.setDuration(duration);
          blurAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {

              subscriber.onNext(AnimationBlurOnSubscribe.this.quickBlur(source,
                  (Integer) animation.getAnimatedValue()));
            }
          });

          blurAnimator.start();
        }
      }

      @Override public void onBitmapFailed(Drawable errorDrawable) {
        if (!subscriber.isUnsubscribed()) subscriber.onError(new PicassoError(errorDrawable));
      }
    };

    Picasso.with(context)//
        .load(resId)//
        .noFade()//
        .config(Bitmap.Config.ARGB_8888)//
        .memoryPolicy(NO_CACHE, NO_STORE).into(target);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        Picasso.with(context).cancelRequest(target);

        if (AnimationBlurOnSubscribe.this.blurAnimator != null) {
          AnimationBlurOnSubscribe.this.blurAnimator.removeAllUpdateListeners();
          AnimationBlurOnSubscribe.this.blurAnimator.cancel();
        }

        AnimationBlurOnSubscribe.this.context = null;
      }
    });
  }

  private Bitmap quickBlur(Bitmap sourceBitmap, int factor) {
    if (factor <= 0) {
      return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    return Bitmap.createScaledBitmap(sourceBitmap, sourceBitmap.getWidth() / factor,
        sourceBitmap.getHeight() / factor, true);
  }
}
