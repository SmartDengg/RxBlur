package com.joker.blurapplication.activity.bluractivity.renderscript;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
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

public class GlideBlurActivity extends BaseActivity implements BlurInterface {

  private static final String TAG = GlideBlurActivity.class.getSimpleName();

  public static void navigateToGlideBlur(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, GlideBlurActivity.class);
    startingActivity.startActivity(intent);
  }

  @Override public void loadBlurBitmap() {

    subscription.add(RxBlurEffective.renderScriptGlideBlur(GlideBlurActivity.this, DRAWABLE_ID, BLUR_RADIUS)
        .timestamp()
        .flatMap(new Func1<Timestamped<GlideDrawable>, Observable<Integer>>() {
          @Override public Observable<Integer> call(Timestamped<GlideDrawable> timestamped) {

            int count = (int) (timestamped.getTimestampMillis() - startTime);

            GlideBlurActivity.this.removeLoading();

            Drawable[] layers = new Drawable[2];
            layers[0] = GlideBlurActivity.this.getResources().getDrawable(DRAWABLE_ID);
            layers[1] = timestamped.getValue();

            TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
            GlideBlurActivity.this.blurIv.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(count);

            return Observable.range(0, count, Schedulers.computation())
                .compose(GlideBlurActivity.this.<Integer>bindUntilEvent(ActivityEvent.DESTROY));
          }
        })
        .onBackpressureBuffer()
        .compose(SchedulersCompat.<Integer>observeOnMainThread())
        .subscribe(new SimpleSubscriber<Integer>() {
          @Override public void onStart() {
            this.request(1);
            GlideBlurActivity.this.startTime = System.currentTimeMillis();
          }

          @Override public void onNext(Integer integer) {
            durationTv.setText("" + integer + "ms");
            request(1);
          }

          @Override public void onError(Throwable e) {
            super.onError(e);

            GlideBlurActivity.this.removeLoading();
            if (e instanceof PicassoError) {
              blurIv.setImageDrawable(((PicassoError) e).getErrorDrawable());
            }
          }
        }));
  }

  @Override public int getLayoutId() {
    return R.layout.activity_simple_layout;
  }

  @Override public void setupToolbar() {
    getSupportActionBar().setTitle("Picasso Blur");
  }
}
