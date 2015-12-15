package com.joker.blurapplication.other.transformation;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import com.squareup.picasso.Transformation;

/**
 * Created by Joker on 2015/11/27.
 */
public class PicassoTransformation implements Transformation {

  private static final int UP_LIMIT = 25;
  private static final int LOW_LIMIT = 1;
  private final Context context;
  private final int blurRadius;

  public PicassoTransformation(Context context, int radius) {
    this.context = context;

    if (radius < LOW_LIMIT) {
      this.blurRadius = LOW_LIMIT;
    } else if (radius > UP_LIMIT) {
      this.blurRadius = UP_LIMIT;
    } else {
      this.blurRadius = radius;
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) @Override
  public Bitmap transform(@NonNull Bitmap source) {

    Bitmap blurredBitmap =
        Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(blurredBitmap);
    Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    paint.setAntiAlias(true);
    canvas.drawBitmap(source, 0, 0, paint);

    RenderScript renderScript = RenderScript.create(context);
    Allocation input =
        Allocation.createFromBitmap(renderScript, source, Allocation.MipmapControl.MIPMAP_FULL,
            Allocation.USAGE_SCRIPT);
    Allocation output = Allocation.createTyped(renderScript, input.getType());
    ScriptIntrinsicBlur scriptIntrinsicBlur =
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

    scriptIntrinsicBlur.setInput(input);
    scriptIntrinsicBlur.setRadius(blurRadius);
    scriptIntrinsicBlur.forEach(output);
    output.copyTo(blurredBitmap);

    source.recycle();
    scriptIntrinsicBlur.destroy();

    return blurredBitmap;
  }

  @Override public String key() {
    return "PicassoTransformation";
  }
}
