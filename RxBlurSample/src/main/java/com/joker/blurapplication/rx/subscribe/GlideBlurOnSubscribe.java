package com.joker.blurapplication.rx.subscribe;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.internal.Preconditions;
import com.joker.blurapplication.other.IntegerVersionSignature;
import com.joker.blurapplication.other.transformation.GlideTransformation;
import com.joker.blurapplication.other.util.ExecutorUtil;
import java.util.concurrent.Callable;
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

  private MyFutureTask futureTask = null;

  private Handler mainHandler = new Handler(Looper.getMainLooper());

  public GlideBlurOnSubscribe(@NonNull Context context, @DrawableRes int resId, int radius) {
    this.context = context;
    this.resId = resId;
    this.radius = radius;
    this.cacheExecutor = ExecutorUtil.getSingleThreadExecutor();
  }

  @SuppressWarnings("unchecked")
  @Override public void call(final Subscriber<? super GlideDrawable> subscriber) {
    Preconditions.checkUiThread();

    if (!subscriber.isUnsubscribed()) {
      futureTask = new MyFutureTask(new MyCallback(), subscriber);
      cacheExecutor.execute(futureTask);
      subscriber.add(Subscriptions.from(futureTask));
    }

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {

        if (futureTask != null) {
          futureTask.callback.getFutureTarget().clear();
        }

        mainHandler.removeCallbacksAndMessages(null);
        GlideBlurOnSubscribe.this.context = null;
      }
    });
  }

  class MyCallback implements Callable<GlideDrawable> {

    private FutureTarget<GlideDrawable> futureTarget;

    public FutureTarget<GlideDrawable> getFutureTarget() {
      return futureTarget;
    }

    @Override public GlideDrawable call() throws Exception {

      futureTarget = Glide
          .with(context)
          .load(resId)
          .signature(new IntegerVersionSignature(System.identityHashCode(System.currentTimeMillis())))
          .bitmapTransform(new GlideTransformation(context, Glide.get(context).getBitmapPool(), radius))
          .into(-1, -1);

      return futureTarget.get();
    }
  }

  class MyFutureTask extends FutureTask<GlideDrawable> {

    private MyCallback callback;
    private Subscriber<? super GlideDrawable> subscriber;

    public MyFutureTask(Callable<GlideDrawable> callable, Subscriber<? super GlideDrawable> subscriber) {
      super(callable);
      this.callback = (MyCallback) callable;
      this.subscriber = subscriber;
    }

    @Override protected void done() {
      mainHandler.post(new Runnable() {
        @Override public void run() {
          try {
            if (isDone() && !isCancelled()) {
              subscriber.onNext(MyFutureTask.this.get());
            }
          } catch (InterruptedException | ExecutionException e) {
            subscriber.onError(e);
          }
        }
      });
    }
  }
}
