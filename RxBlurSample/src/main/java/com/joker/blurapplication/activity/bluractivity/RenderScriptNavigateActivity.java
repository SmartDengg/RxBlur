package com.joker.blurapplication.activity.bluractivity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import butterknife.OnClick;
import com.joker.blurapplication.R;
import com.joker.blurapplication.activity.BaseActivity;
import com.joker.blurapplication.activity.bluractivity.renderscript.GlideBlurActivity;
import com.joker.blurapplication.activity.bluractivity.renderscript.PicassoBlurActivity;
import com.joker.blurapplication.activity.bluractivity.renderscript.RemoteBlurActivity;

/**
 * Created by Joker on 2015/12/5.
 */
public class RenderScriptNavigateActivity extends BaseActivity {

  public static void navigateToRenderScriptNavigate(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, RenderScriptNavigateActivity.class);
    startingActivity.startActivity(intent);
  }

  @Nullable @OnClick(R.id.navigate_layout_picasso_btn) void onPicassoBlurClick() {
    PicassoBlurActivity.navigateToPicassoBlur(RenderScriptNavigateActivity.this);
  }

  @Nullable @OnClick(R.id.navigate_layout_glide_btn) void onGlideBlurClick() {
    GlideBlurActivity.navigateToGlideBlur(RenderScriptNavigateActivity.this);
  }


  @Nullable @OnClick(R.id.navigate_layout_remote_btn) void onRemoteBlurClick() {
    RemoteBlurActivity.navigateToRemoteBlur(RenderScriptNavigateActivity.this);
  }

  @Override public int getLayoutId() {
    return R.layout.render_script_navigate_layout;
  }

  @Override public void setupActionBar() {
    getSupportActionBar().setTitle("RenderScript Navigator");
  }
}
