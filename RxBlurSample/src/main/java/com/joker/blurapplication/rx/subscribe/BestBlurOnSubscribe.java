package com.joker.blurapplication.rx.subscribe;

import android.content.Context;
import android.graphics.Bitmap;
import com.joker.blurapplication.other.util.ExecutorUtil;
import com.joker.blurapplication.other.BestBlur;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by Joker on 2015/11/30.
 */
public class BestBlurOnSubscribe implements Observable.OnSubscribe<Bitmap> {

  private Bitmap bitmap;
  private int radius;
  private float desaturateAmount;
  private BestBlur bestBlur;
  private Executor synchronousExecutor;

  public BestBlurOnSubscribe(Context context, Bitmap bitmap, int radius, float desaturateAmount) {
    this.bitmap = bitmap;
    this.radius = radius;
    this.desaturateAmount = desaturateAmount;
    this.synchronousExecutor = ExecutorUtil.getSynchronousExecutor();
    this.bestBlur = new BestBlur(context);
  }

  @Override public void call(Subscriber<? super Bitmap> subscriber) {

    if (!subscriber.isUnsubscribed()) {
      FutureTask<Bitmap> futureTask =
          new FutureTask<>(BestBlurOnSubscribe.this.futureRunnable(bitmap, subscriber), null);
      synchronousExecutor.execute(futureTask);
      subscriber.add(Subscriptions.from(futureTask));
    }
  }

  private Runnable futureRunnable(final Bitmap sourceBitmap,
      final Subscriber<? super Bitmap> subscriber) {

    return new Runnable() {
      @Override public void run() {

        if (!subscriber.isUnsubscribed()) {
          Bitmap blurBitmap = bestBlur.blurBitmap(sourceBitmap, radius, desaturateAmount);
          subscriber.onNext(blurBitmap);

          bestBlur.destroy();
        }
      }
    };
  }
}
