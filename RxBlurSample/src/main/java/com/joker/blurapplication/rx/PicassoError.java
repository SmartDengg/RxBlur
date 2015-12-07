package com.joker.blurapplication.rx;

import android.graphics.drawable.Drawable;

/**
 * Created by Joker on 2015/11/27.
 */
public class PicassoError extends Exception {

  private Drawable errorDrawable;

  public PicassoError(Drawable errorDrawable) {
    this.errorDrawable = errorDrawable;
  }

  public Drawable getErrorDrawable() {
    return errorDrawable;
  }
}
