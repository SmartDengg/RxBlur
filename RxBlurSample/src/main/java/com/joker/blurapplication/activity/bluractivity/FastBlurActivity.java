package com.joker.blurapplication.activity.bluractivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.joker.blurapplication.R;
import com.joker.blurapplication.activity.BaseActivity;
import com.joker.blurapplication.activity.BlurInterface;
import com.joker.blurapplication.rxkit.PicassoError;
import com.joker.blurapplication.rxkit.RxBlurEffective;
import com.joker.blurapplication.rxkit.SchedulersCompat;
import com.joker.blurapplication.rxkit.SimpleSubscriber;
import com.trello.rxlifecycle.ActivityEvent;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;

public class FastBlurActivity extends BaseActivity implements BlurInterface {

  private static final String TAG = FastBlurActivity.class.getSimpleName();

  public static void navigateToFastBlur(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, FastBlurActivity.class);
    startingActivity.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FastBlurActivity.this.loadBlurBitmap();
  }

  @Override public void loadBlurBitmap() {

    subscription.add(RxBlurEffective
                         .fastBlur(FastBlurActivity.this, DRAWABLE_ID, BLUR_RADIUS)
                         .timestamp()
                         .flatMap(new Func1<Timestamped<Bitmap>, Observable<Integer>>() {
                           @SuppressWarnings("deprecation") @Override
                           public Observable<Integer> call(Timestamped<Bitmap> timestamped) {

                             int count = (int) (timestamped.getTimestampMillis() - startTime);

                             FastBlurActivity.this.removeLoading();

                             BitmapDrawable bitmapDrawable =
                                 new BitmapDrawable(FastBlurActivity.this.getResources(), timestamped.getValue());

                              Drawable[] layers = new Drawable[] {
                                 FastBlurActivity.this.getResources().getDrawable(DRAWABLE_ID), bitmapDrawable
                             };

                             TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
                             FastBlurActivity.this.blurIv.setImageDrawable(transitionDrawable);
                             transitionDrawable.startTransition(count);

                             return Observable
                                 .range(0, count, Schedulers.computation())
                                 .compose(FastBlurActivity.this.<Integer>bindUntilEvent(ActivityEvent.DESTROY));
                           }
                         })
                         .onBackpressureBuffer()
                         .compose(SchedulersCompat.<Integer>observeOnMainThread())
                         .subscribe(new SimpleSubscriber<Integer>() {
                           @Override public void onStart() {
                             this.request(1);
                             FastBlurActivity.this.startTime = System.currentTimeMillis();
                           }

                           @Override public void onNext(Integer integer) {
                             durationTv.setText("" + integer + "ms");
                             request(1);
                           }

                           @Override public void onError(Throwable e) {
                             super.onError(e);

                             FastBlurActivity.this.removeLoading();
                             if (e instanceof PicassoError) {
                               blurIv.setImageDrawable(((PicassoError) e).getErrorDrawable());
                             }
                           }
                         }));
  }

  @Override public int getLayoutId() {
    return R.layout.activity_simple_layout;
  }

  @Override public void setupActionBar() {
    getSupportActionBar().setTitle("Fast Blur");
  }
}
