package com.joker.blurapplication.other;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Joker on 2015/11/27.
 */
public class TargetAdapter implements Target {
  @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

  }

  @Override public void onBitmapFailed(Drawable errorDrawable) {

  }

  @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

  }
}
