package com.joker.blurapplication.activity.bluractivity.renderscript;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import com.joker.blurapplication.R;
import com.joker.blurapplication.activity.BaseActivity;
import com.joker.blurapplication.activity.BlurInterface;
import com.joker.blurapplication.rx.PicassoError;
import com.joker.blurapplication.rx.RxBlurEffective;
import com.joker.blurapplication.rx.SchedulersCompat;
import com.joker.blurapplication.rx.SimpleSubscriber;
import com.trello.rxlifecycle.ActivityEvent;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;

public class PicassoBlurActivity extends BaseActivity implements BlurInterface {

  private static final String TAG = PicassoBlurActivity.class.getSimpleName();

  public static void navigateToPicassoBlur(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, PicassoBlurActivity.class);
    startingActivity.startActivity(intent);
  }

  @Override public void loadBlurBitmap() {

    subscription.add(RxBlurEffective
                         .renderScriptPicassoBlur(PicassoBlurActivity.this, DRAWABLE_ID, BLUR_RADIUS)
                         .timestamp()
                         .flatMap(new Func1<Timestamped<Bitmap>, Observable<Integer>>() {
                           @Override public Observable<Integer> call(Timestamped<Bitmap> timestamped) {

                             int count = (int) (timestamped.getTimestampMillis() - startTime);
                             PicassoBlurActivity.this.removeLoading();

                             BitmapDrawable bitmapDrawable =
                                 new BitmapDrawable(PicassoBlurActivity.this.getResources(), timestamped.getValue());

                             Drawable[] layers = new Drawable[2];
                             layers[0] = PicassoBlurActivity.this.getResources().getDrawable(DRAWABLE_ID);
                             layers[1] = bitmapDrawable;

                             TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
                             PicassoBlurActivity.this.blurIv.setImageDrawable(transitionDrawable);
                             transitionDrawable.startTransition(count);

                             return Observable
                                 .range(0, count, Schedulers.computation())
                                 .compose(PicassoBlurActivity.this.<Integer>bindUntilEvent(ActivityEvent.DESTROY));
                           }
                         })
                         .onBackpressureBuffer()
                         .compose(SchedulersCompat.<Integer>observeOnMainThread())
                         .subscribe(new SimpleSubscriber<Integer>() {
                           @Override public void onStart() {
                             this.request(1);
                             PicassoBlurActivity.this.startTime = System.currentTimeMillis();
                           }

                           @Override public void onNext(Integer integer) {
                             durationTv.setText("" + integer + "ms");
                             this.request(1);
                           }

                           @Override public void onError(Throwable e) {
                             super.onError(e);

                             PicassoBlurActivity.this.removeLoading();
                             if (e instanceof PicassoError) {
                               blurIv.setImageDrawable(((PicassoError) e).getErrorDrawable());
                             }
                           }
                         }));
  }

 /* private <T> Observable.Transformer<T, T> transformer() {
    return new Observable.Transformer<T, T>() {
      @Override public Observable<T> call(Observable<T> sourceObservable) {
        return sourceObservable.takeUntil(
            RxBlurEffective.renderScriptPicassoBlur(PicassoBlurActivity.this, R.drawable.template,
                16)).map(new Func1<T, T>() {
          @Override public T call(T t) {
            if (t instanceof Bitmap) blurIv.setImageBitmap((Bitmap) t);
            return t;
          }
        }).subscribeOn(AndroidSchedulers.mainThread());
      }
    };
  }*/

  @Override public int getLayoutId() {
    return R.layout.activity_simple_layout;
  }

  @Override public void setupActionBar() {
    getSupportActionBar().setTitle("Picasso Blur");
  }
}
