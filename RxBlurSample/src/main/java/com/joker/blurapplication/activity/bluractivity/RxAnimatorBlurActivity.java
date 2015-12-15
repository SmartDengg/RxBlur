package com.joker.blurapplication.activity.bluractivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.OnClick;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.joker.blurapplication.R;
import com.joker.blurapplication.activity.LazyBaseActivity;
import com.joker.blurapplication.activity.BlurInterface;
import com.joker.blurapplication.rx.PicassoError;
import com.joker.blurapplication.rx.RxBlurEffective;
import com.joker.blurapplication.rx.SimpleSubscriber;
import com.joker.blurapplication.rx.rxandroid.schedulers.AndroidSchedulers;
import com.joker.blurapplication.rx.SchedulersCompat;
import com.trello.rxlifecycle.ActivityEvent;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Joker on 2015/11/28.
 */
public class RxAnimatorBlurActivity extends LazyBaseActivity implements BlurInterface {

  private static final String TAG = RxAnimatorBlurActivity.class.getSimpleName();

  @Nullable @Bind(R.id._layout_animator_iv) protected ImageView blurIv;
  @Nullable @Bind(R.id._layout_radius_et) protected EditText radiusEt;
  @Nullable @Bind(R.id._layout_duration_et) protected EditText durationEt;
  @Nullable @Bind(R.id._layout_blur_btn) protected Button blurBtn;

  private String radius, duration;

  private CompositeSubscription subscription = new CompositeSubscription();

  public static void navigateToRxAnimatorBlur(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, RxAnimatorBlurActivity.class);
    startingActivity.startActivity(intent);
  }

  @Override public void initDrawable() {

    Drawable drawable = getResources().getDrawable(R.drawable.template);

    final int intrinsicWidth = drawable.getIntrinsicWidth();
    final int intrinsicHeight = drawable.getIntrinsicHeight();

    Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
    Paint paint = new Paint();
    paint.setFlags(Paint.FILTER_BITMAP_FLAG);

    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
    drawable.draw(canvas);

    ViewGroup.LayoutParams layoutParams = blurIv.getLayoutParams();
    layoutParams.height = intrinsicHeight;

    blurIv.setImageBitmap(bitmap);
  }

  @Override public void setListener() {

    Observable<TextViewTextChangeEvent> radiusObservable =
        RxTextView.textChangeEvents(radiusEt).skip(1);
    Observable<TextViewTextChangeEvent> durationObservable =
        RxTextView.textChangeEvents(durationEt).skip(1);

    Observable.combineLatest(radiusObservable, durationObservable,
        new Func2<TextViewTextChangeEvent, TextViewTextChangeEvent, Boolean>() {
          @Override public Boolean call(TextViewTextChangeEvent radiusEvent,
              TextViewTextChangeEvent durationEvent) {

            radius = radiusEvent.text().toString();
            duration = durationEvent.text().toString();

            return !TextUtils.isEmpty(radius) && !TextUtils.isEmpty(duration);
          }
        })
        .debounce(300, TimeUnit.MILLISECONDS)
        .compose(RxAnimatorBlurActivity.this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .startWith(false)
        .subscribe(new Action1<Boolean>() {
          @Override public void call(Boolean aBoolean) {
            blurBtn.setEnabled(aBoolean);
          }
        });
  }

  @Override public void loadBlurBitmap() {

    RxAnimatorBlurActivity.this.unsubscribe();

    subscription.add(RxBlurEffective.animatorBlur(RxAnimatorBlurActivity.this, R.drawable.template,
        Integer.parseInt(radius), Long.parseLong(duration))
        .compose(SchedulersCompat.<Bitmap>observeOnMainThread())
        .subscribe(new SimpleSubscriber<Bitmap>() {
          @Override public void onStart() {
            this.request(1);
          }

          @Override public void onNext(Bitmap bitmap) {
            blurIv.setImageBitmap(bitmap);
            this.request(1);
          }

          @Override public void onError(Throwable e) {
            super.onError(e);

            if (e instanceof PicassoError) {
              blurIv.setImageDrawable(((PicassoError) e).getErrorDrawable());
            }
          }
        }));
  }

  @Nullable @OnClick(R.id._layout_blur_btn) public void onBlurClick() {
    RxAnimatorBlurActivity.this.loadBlurBitmap();
  }

  @Override protected void onDestroy() {
    RxAnimatorBlurActivity.this.unsubscribe();
    super.onDestroy();
  }

  private void unsubscribe() {
    if (subscription != null && !subscription.isUnsubscribed()) subscription.clear();
  }

  @Override public int getLayoutId() {
    return R.layout.activity_animator_layout;
  }

  @Override public void setupToolbar() {
    getSupportActionBar().setTitle("Rx Animation Blur");
  }
}
