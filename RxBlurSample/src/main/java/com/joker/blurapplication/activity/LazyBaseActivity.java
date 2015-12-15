package com.joker.blurapplication.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import butterknife.ButterKnife;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by Joker on 2015/11/27.
 */
public abstract class LazyBaseActivity extends RxAppCompatActivity {

  @Override public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.bind(LazyBaseActivity.this);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(LazyBaseActivity.this.getLayoutId());
    LazyBaseActivity.this.setupToolbar();

    LazyBaseActivity.this.initDrawable();
    LazyBaseActivity.this.setListener();
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      LazyBaseActivity.this.finish();
    }
    return false;
  }


  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(LazyBaseActivity.this);
  }

  protected abstract int getLayoutId();

  protected abstract void setupToolbar();

  protected abstract void initDrawable();

  protected abstract void setListener();
}
