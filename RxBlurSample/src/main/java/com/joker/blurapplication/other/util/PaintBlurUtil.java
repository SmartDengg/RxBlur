package com.joker.blurapplication.other.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Joker on 2015/11/28.
 */
public class PaintBlurUtil {

  private static final Paint paint = new Paint();

  public static Bitmap blur(Bitmap sourceBitmap, int radius) {

    final Bitmap blurBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
        Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(blurBitmap);

    paint.setAlpha(180);
    canvas.drawBitmap(sourceBitmap, 0, 0, paint);

    for (int row = -radius; row < radius; row += 2) {

      for (int col = -radius; col < radius; col += 2) {

        if (col * col + row * row <= radius * radius) {

          paint.setAlpha((radius * radius) / ((col * col + row * row) + 1) * 2);
          canvas.drawBitmap(sourceBitmap, row, col, paint);
        }
      }
    }
    sourceBitmap.recycle();
    return blurBitmap;
  }
}
