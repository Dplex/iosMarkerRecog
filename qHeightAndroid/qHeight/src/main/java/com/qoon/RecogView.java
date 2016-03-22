package com.qoon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RecogView extends View {

  boolean flag;
  Paint paint;

  public RecogView(Context context, AttributeSet attrs) {
    super(context, attrs);
    paint = new Paint();
    paint.setStrokeWidth(5);
  }

  public void setFlag(boolean flag) {
    this.flag = flag;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (flag == false)
      paint.setColor(Color.RED);
    else
      paint.setColor(Color.GREEN);
    float pixel = MainActivity.pixelFromDP(40);
    canvas.drawCircle(40 / pixel * 10, 40 / pixel * 10, 40 / pixel * 5, paint);
    super.onDraw(canvas);
  }
}
