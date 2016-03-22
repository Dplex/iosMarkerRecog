package com.qoon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

public class SensorView extends View implements SensorEventListener {

  SensorManager sm;
  Sensor sensor;
  float v2, v3;
  Paint paint;
  int width;
  int height;
  private RecogView sensorRecogView = null;

  public SensorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    paint = new Paint();
    if (!isInEditMode()) {
      sm = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
      sensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
      sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
      paint.setColor(Color.GREEN);
    }
  }

  public SensorView(Context context) {
    super(context);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    width = MeasureSpec.getSize(widthMeasureSpec);
    height = MeasureSpec.getSize(heightMeasureSpec);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    int x = (int) v3;
    int y = (int) (v2 + 80);
    if ((y >= 72 && y <= 88)) {
      paint.setColor(Color.GREEN);
      if (sensorRecogView != null)
        sensorRecogView.setFlag(true);
    } else {
      paint.setColor(Color.RED);
      if (sensorRecogView != null)
        sensorRecogView.setFlag(false);
    }
    int posy = y * height / 180;
    int posx = width / 2;

    float pixel = MainActivity.pixelFromDP(20);

    canvas.drawCircle(x + posx, posy + 20 / pixel * 5, 20 / pixel * 5, paint);
    paint.setTextSize(45);
    if (sensorRecogView != null)
      sensorRecogView.invalidate();
    super.onDraw(canvas);
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == sensor.TYPE_ORIENTATION) {
      v2 = event.values[1];
      v3 = event.values[2];
      this.invalidate();
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  public void setFlagParent(RecogView sensorRecogView) {
    this.sensorRecogView = sensorRecogView;
  }
}