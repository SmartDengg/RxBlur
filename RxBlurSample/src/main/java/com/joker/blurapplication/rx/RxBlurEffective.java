package com.joker.blurapplication.rx;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.joker.blurapplication.rx.subscribe.AnimationBlurOnSubscribe;
import com.joker.blurapplication.rx.subscribe.BestBlurOnSubscribe;
import com.joker.blurapplication.rx.subscribe.FastBlurOnSubscribe;
import com.joker.blurapplication.rx.subscribe.GlideBlurOnSubscribe;
import com.joker.blurapplication.rx.subscribe.PaintBlurOnSubscribe;
import com.joker.blurapplication.rx.subscribe.PicassoBlurOnSubscribe;
import rx.Observable;

/**
 * Created by Joker on 2015/11/27.
 */
public class RxBlurEffective {

  @CheckResult @NonNull
  public static Observable<Bitmap> renderScriptPicassoBlur(@NonNull Context context, @DrawableRes int resId,
      int radius) {
    return Observable.create(new PicassoBlurOnSubscribe(context, resId, radius));
  }

  @CheckResult @NonNull
  public static Observable<GlideDrawable> renderScriptGlideBlur(@NonNull Context context, @DrawableRes int resId,
      int radius) {
    return Observable.create(new GlideBlurOnSubscribe(context, resId, radius));
  }

  @CheckResult @NonNull
  public static Observable<Bitmap> fastBlur(@NonNull Context context, @DrawableRes int resId, int radius) {
    return Observable
        .create(new FastBlurOnSubscribe(context, resId, radius))
        .compose(SchedulersCompat.<Bitmap>observeOnMainThread());
  }

  @CheckResult @NonNull
  public static Observable<Bitmap> paintBlur(@NonNull Context context, @DrawableRes int resId, int radius) {
    return Observable
        .create(new PaintBlurOnSubscribe(context, resId, radius))
        .compose(SchedulersCompat.<Bitmap>observeOnMainThread());
  }

  @CheckResult @NonNull
  public static Observable<Bitmap> animatorBlur(@NonNull Context context, @DrawableRes int resId, int radius,
      long duration) {
    return Observable.create(new AnimationBlurOnSubscribe(context, resId, radius, duration)).onBackpressureBuffer();
  }

  @CheckResult @NonNull
  public static Observable<Bitmap> bestBlur(@NonNull Context context, @DrawableRes Bitmap bitmap, int radius,
      float desaturateAmount) {
    return Observable.create(new BestBlurOnSubscribe(context, bitmap, radius, desaturateAmount));
  }
}
