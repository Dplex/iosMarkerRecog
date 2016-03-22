package com.qoon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class MarkerDrawView extends View {

  public static float markerX[] = new float[2];
  public static float markerY[] = new float[2];
  Paint _paint;
  float[] x;
  float[] y;
  private RecogView markerRecogView;

  public MarkerDrawView(Context context) {
    super(context);
    _paint = new Paint();
    _paint.setAntiAlias(true);
    _paint.setStrokeWidth(5);
    x = new float[4];
    y = new float[4];
    _paint.setColor(Color.GREEN);
  }

  protected void onDraw(Canvas canvas) {

    for (int i = 0; i < 4; i++) {
      x[i] *= canvas.getWidth();
      y[i] *= canvas.getHeight();
    }
    markerX[0] = x[1];
    markerX[1] = x[2];
    markerY[0] = y[1];
    markerY[1] = y[2];

    if (!isMarkerRecognized()) {
      markerRecogView.setFlag(false);
    } else {
      markerRecogView.setFlag(true);
      for (int i = 0; i < 4; i++) {
        canvas.drawCircle(x[i], y[i], 10, _paint);
      }
      Log.i("JHR", x[0] + "," + x[1] + "," + x[2] + "," + x[3]);
    }

    markerRecogView.invalidate();

  }

  public static boolean isMarkerRecognized() {
    if ((markerX[0] == markerX[1]) && (markerY[0] == markerY[1])) {
      return false;
    }
    return true;
  }

  public void setFlagParent(RecogView markerRecogView) {
    this.markerRecogView = markerRecogView;
  }
}
