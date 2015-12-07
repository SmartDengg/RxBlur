package com.joker.blurapplication;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by Joker on 2015/12/5.
 */
public class GlideConfiguration implements GlideModule {

  @Override public void applyOptions(Context context, GlideBuilder builder) {
    builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
  }

  @Override public void registerComponents(Context context, Glide glide) {

  }
}