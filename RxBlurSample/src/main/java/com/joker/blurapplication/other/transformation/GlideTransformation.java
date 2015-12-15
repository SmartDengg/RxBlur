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
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import java.lang.ref.WeakReference;

/**
 * Created by Joker on 2015/12/5.
 */
public class GlideTransformation implements Transformation<Bitmap> {

  private static final int UP_LIMIT = 25;
  private static final int LOW_LIMIT = 1;
  private final WeakReference<Context> weakReference;
  private final int blurRadius;
  private final BitmapPool bitmapPool;

  public GlideTransformation(Context context, BitmapPool bitmapPool, int radius) {
    this.weakReference = new WeakReference<>(context);
    this.bitmapPool = bitmapPool;

    if (radius < LOW_LIMIT) {
      this.blurRadius = LOW_LIMIT;
    } else if (radius > UP_LIMIT) {
      this.blurRadius = UP_LIMIT;
    } else {
      this.blurRadius = radius;
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) @Override
  public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {

    Bitmap sourceBitmap = resource.get();

    int width = sourceBitmap.getWidth();
    int height = sourceBitmap.getHeight();

    Bitmap blurBitmap = bitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
    if (blurBitmap == null) {
      blurBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    Canvas canvas = new Canvas(blurBitmap);
    Paint paint = new Paint();
    paint.setFlags(Paint.FILTER_BITMAP_FLAG);
    canvas.drawBitmap(sourceBitmap, 0, 0, paint);

    if (weakReference.get() != null) {
      RenderScript rs = RenderScript.create(weakReference.get());
      Allocation input =
          Allocation.createFromBitmap(rs, blurBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
      Allocation output = Allocation.createTyped(rs, input.getType());
      ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

      blur.setInput(input);
      blur.setRadius(blurRadius);
      blur.forEach(output);
      output.copyTo(blurBitmap);

      rs.destroy();
    }

    return BitmapResource.obtain(blurBitmap, bitmapPool);
  }

  @Override public String getId() {
    return "GlideTransformation";
  }
}
