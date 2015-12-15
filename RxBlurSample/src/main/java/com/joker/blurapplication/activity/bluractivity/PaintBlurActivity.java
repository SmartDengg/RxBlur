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
import com.joker.blurapplication.rx.PicassoError;
import com.joker.blurapplication.rx.RxBlurEffective;
import com.joker.blurapplication.rx.SchedulersCompat;
import com.joker.blurapplication.rx.SimpleSubscriber;
import com.trello.rxlifecycle.ActivityEvent;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;

public class PaintBlurActivity extends BaseActivity implements BlurInterface {

  private static final String TAG = PaintBlurActivity.class.getSimpleName();

  public static void navigateToPaintBlur(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, PaintBlurActivity.class);
    startingActivity.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    PaintBlurActivity.this.loadBlurBitmap();
  }

  @Override public void loadBlurBitmap() {

    subscription.add(RxBlurEffective.paintBlur(PaintBlurActivity.this, DRAWABLE_ID, BLUR_RADIUS)//
        .timestamp()
        .flatMap(new Func1<Timestamped<Bitmap>, Observable<Integer>>() {
          @Override public Observable<Integer> call(Timestamped<Bitmap> timestamped) {

            int count = (int) (timestamped.getTimestampMillis() - startTime);

            PaintBlurActivity.this.removeLoading();

            BitmapDrawable bitmapDrawable =
                new BitmapDrawable(PaintBlurActivity.this.getResources(), timestamped.getValue());

            Drawable[] layers = new Drawable[] {
                PaintBlurActivity.this.getResources().getDrawable(DRAWABLE_ID), bitmapDrawable
            };

            TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
            PaintBlurActivity.this.blurIv.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(count);

            return Observable.range(0, count, Schedulers.computation())
                .compose(PaintBlurActivity.this.<Integer>bindUntilEvent(ActivityEvent.DESTROY));
          }
        })
        .onBackpressureBuffer()
        .compose(SchedulersCompat.<Integer>observeOnMainThread())
        .subscribe(new SimpleSubscriber<Integer>() {
          @Override public void onStart() {
            this.request(1);
            PaintBlurActivity.this.startTime = System.currentTimeMillis();
          }

          @Override public void onNext(Integer integer) {
            durationTv.setText("" + integer + "ms");
            request(1);
          }

          @Override public void onError(Throwable e) {
            super.onError(e);

            PaintBlurActivity.this.removeLoading();
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
