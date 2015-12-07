package com.joker.blurapplication.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.joker.blurapplication.R;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Joker on 2015/11/27.
 */
public abstract class BaseActivity extends RxAppCompatActivity {

  protected static final int BLUR_RADIUS = 18;
  protected static final int DRAWABLE_ID = R.drawable.template;

  @Nullable @Bind(R.id._layout_picasso_iv) protected ImageView blurIv;
  @Nullable @Bind(R.id._layout_duration_tv) protected TextView durationTv;
  @Nullable @Bind(R.id.progressbar) protected ProgressBar progressBar;

  protected long startTime = 0L;
  protected CompositeSubscription subscription = new CompositeSubscription();

  @Override public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.bind(BaseActivity.this);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(BaseActivity.this.getLayoutId());
    BaseActivity.this.setupToolbar();

    if (BaseActivity.this instanceof BlurInterface) {
      ((BlurInterface) BaseActivity.this).loadBlurBitmap();
    }
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      BaseActivity.this.finish();
    }
    return false;
  }

  protected void removeLoading() {
    ViewParent parent = progressBar.getParent();
    if (parent != null) {
      ((ViewGroup) parent).removeView(progressBar);
    }
  }

  @Override protected void onDestroy() {
    BaseActivity.this.unSubscribe();
    super.onDestroy();
    ButterKnife.unbind(BaseActivity.this);
  }

  protected void unSubscribe() {
    if (subscription != null && !subscription.isUnsubscribed()) subscription.clear();
  }

  public abstract int getLayoutId();

  public abstract void setupToolbar();
}
