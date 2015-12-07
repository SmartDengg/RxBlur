package com.joker.blurapplication.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.OnClick;
import com.jakewharton.rxbinding.view.RxView;
import com.joker.blurapplication.R;
import com.joker.blurapplication.activity.bluractivity.FastBlurActivity;
import com.joker.blurapplication.activity.bluractivity.PaintBlurActivity;
import com.joker.blurapplication.activity.bluractivity.RenderScriptNavigateActivity;
import com.joker.blurapplication.activity.bluractivity.RxAnimatorBlurActivity;
import com.joker.blurapplication.rx.RxBlurEffective;
import com.joker.blurapplication.rx.rxviewstub.RxViewStub;
import com.joker.blurapplication.rx.SchedulersCompat;
import com.joker.blurapplication.rx.SimpleSubscriber;
import com.joker.blurapplication.rx.rxviewstub.ViewStubEvent;
import com.trello.rxlifecycle.ActivityEvent;
import java.util.concurrent.TimeUnit;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Joker on 2015/11/28.
 */
public class MainActivity extends BaseActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Nullable @Bind(R.id.main_viewstub_layout) ViewStub viewStub;

  @Nullable @OnClick(R.id.main_layout_render_btn) public void onRenderScripNavigatorClick() {
    RenderScriptNavigateActivity.navigateToRenderScriptNavigate(MainActivity.this);
  }

  @Nullable @OnClick(R.id.main_layout_fast_btn) public void onFastBlurClick() {
    FastBlurActivity.navigateToFastBlur(MainActivity.this);
  }

  @Nullable @OnClick(R.id.main_layout_paint_btn) public void onPaintBlurClick() {
    PaintBlurActivity.navigateToPaintBlur(MainActivity.this);
  }

  @Nullable @OnClick(R.id.main_layout_animator_btn) public void onRxAnimatorBlurClick() {
    RxAnimatorBlurActivity.navigateToRxAnimatorBlur(MainActivity.this);
  }

  @Nullable @OnClick(R.id.main_layout_btn) public void onBestBlurClick() {

    if (viewStub.getParent() != null) {
      viewStub.inflate();
    } else {
      viewStub.setVisibility(View.VISIBLE);
    }
  }

  private Bitmap cacheCurrentScreen() {

    View root = MainActivity.this.getWindow().getDecorView().findViewById(android.R.id.content);
    root.setDrawingCacheEnabled(true);
    Bitmap drawingCache = root.getDrawingCache();

    Bitmap currentBitmap = Bitmap.createBitmap(drawingCache.getWidth(), drawingCache.getHeight(),
        Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(currentBitmap);
    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setFlags(Paint.FILTER_BITMAP_FLAG);
    canvas.drawBitmap(drawingCache, 0, 0, paint);

    drawingCache.recycle();

    return currentBitmap;
  }

  @Override public int getLayoutId() {
    return R.layout.main_layout;
  }

  @Override public void setupToolbar() {
    getSupportActionBar().setTitle("BlurApplication");

    RxViewStub.inflateEvents(viewStub).map(new Func1<ViewStubEvent, ImageView>() {
      @Override public ImageView call(ViewStubEvent viewStubEvent) {
        return (ImageView) viewStubEvent.getInflated();
      }
    }).map(new Func1<ImageView, Void>() {
      @Override public Void call(ImageView blurImageView) {

        RxView.clicks(blurImageView)
            .debounce(300, TimeUnit.MILLISECONDS)
            .compose(SchedulersCompat.observeOnMainThread())
            .compose(MainActivity.this.bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(subscriber);

        /*0.1f灰度,如果不喜欢可以置零:)*/
        Bitmap blurBitmap =
            RxBlurEffective.bestBlur(MainActivity.this, MainActivity.this.cacheCurrentScreen(), 16,
                0.1f)
                .compose(MainActivity.this.<Bitmap>bindUntilEvent(ActivityEvent.DESTROY))
                .toBlocking()
                .first();

        blurImageView.setImageBitmap(blurBitmap);
        return null;
      }
    }).compose(MainActivity.this.bindUntilEvent(ActivityEvent.DESTROY)).subscribe();
  }

  private Subscriber subscriber = new SimpleSubscriber() {
    @Override public void onNext(Object o) {
      MainActivity.this.viewStub.setVisibility(View.GONE);
    }
  };

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

      View view = findViewById(R.id.main_layout_blur_iv);
      if (view != null && view.getVisibility() == View.VISIBLE) {
        viewStub.setVisibility(View.GONE);
      } else {
        MainActivity.this.finish();
      }
    }
    return false;
  }
}
