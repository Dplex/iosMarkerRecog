package com.qoon;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MeasureActivity extends Activity implements OnClickListener, OnTouchListener {

  public Intent preview;
  Button saveButton;
  Button exitButton;
  private int _yDelta;
  FrameLayout _frameLayout;
  boolean isMarkerRecognized;
  ImageView slider1, slider2, markerSlider1, markerSlider2;
  EditText markerEdit;
  TextView heightText, markerStringText;
  TextView dateText;
  private double distance;
  private double[] markerY;
  private double[] sliderY;
  private double markerDistance = 0;
  private Bitmap bitmap;
  String dateString;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.measure);
    markerY = new double[2];
    sliderY = new double[2];
    isMarkerRecognized = MarkerDrawView.isMarkerRecognized();
    markerSlider1 = (ImageView) findViewById(R.id.markerSlider1);
    markerSlider2 = (ImageView) findViewById(R.id.markerSlider2);
    markerSlider1.setImageAlpha(100);
    markerSlider1.setImageAlpha(100);
    markerEdit = (EditText) findViewById(R.id.markerHeight);
    markerStringText = (TextView) findViewById(R.id.markerStringText);

    _frameLayout = (FrameLayout) findViewById(R.id.frame);

    if (isMarkerRecognized == false) {

      markerSlider1.setVisibility(View.VISIBLE);
      markerSlider2.setVisibility(View.VISIBLE);
      markerEdit.setVisibility(View.VISIBLE);
      markerStringText.setVisibility(View.VISIBLE);
      markerSlider1.setOnTouchListener(this);
      markerSlider2.setOnTouchListener(this);
      markerSlider1.setImageAlpha(100);
      markerSlider2.setImageAlpha(100);

    } else {

      markerSlider1.setVisibility(View.INVISIBLE);
      markerSlider2.setVisibility(View.INVISIBLE);
      markerEdit.setVisibility(View.INVISIBLE);
      markerStringText.setVisibility(View.INVISIBLE);
      ((TextView) findViewById(R.id.removeCm)).setVisibility(View.INVISIBLE);

      ImageView heightIcon = (ImageView) findViewById(R.id.heightIcon);
      TextView textHeight = (TextView) findViewById(R.id.textHeight);

      float bottomMargin = MainActivity.pixelFromDP(20);
      bottomMargin = 20.0f / bottomMargin * 50.0f;

      FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) heightIcon.getLayoutParams();
      param.bottomMargin = (int) bottomMargin;
      heightIcon.setLayoutParams(param);

      RelativeLayout.LayoutParams param2 = (RelativeLayout.LayoutParams) textHeight.getLayoutParams();
      param2.bottomMargin = (int) bottomMargin;
      textHeight.setLayoutParams(param2);

      _frameLayout.invalidate();

    }

    heightText = (TextView) findViewById(R.id.textHeight);
    preview = getIntent();
    ImageView iv = (ImageView) findViewById(R.id.measureView);

    int ivWidth = iv.getWidth();
    int ivHeight = iv.getHeight();

    bitmap = imgRotate(PreviewActivity.getBitmap());
    iv.setImageBitmap(bitmap);
    exitButton = (Button) findViewById(R.id.exit);
    exitButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
      }
    });

    //		MeasureHeight measureHeight = (MeasureHeight)findViewById(R.id.measureHeightView);
    slider1 = (ImageView) findViewById(R.id.slider1);
    slider1.setOnTouchListener(this);
    slider2 = (ImageView) findViewById(R.id.slider2);
    slider2.setOnTouchListener(this);

    saveButton = (Button) findViewById(R.id.save);
    saveButton.setOnClickListener(this);

    Calendar calendar = Calendar.getInstance();
    dateString = String.format("%02d년 %02d월 %02d일",
        calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH));

    dateText = (TextView) findViewById(R.id.dateText);
    dateText.setText(dateString);

    _initGuideButton();
    //		addContentView(new MeasureHeight(getBaseContext(), 1080, (int)(1920 * 0.9)), new LayoutParams(LayoutParams.MATCH_PARENT, (int)(1920 * 0.9)));
  }

  void _initGuideButton() {
    Button btn = (Button) findViewById(R.id.guideButtonInPreview);
    btn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent it = new Intent(getBaseContext(), GuideActivity.class);
        startActivity(it);
      }
    });
  }

  private Bitmap imgRotate(Bitmap bmp) {
    int width = bmp.getWidth();
    int height = bmp.getHeight();
    Matrix matrix = new Matrix();
    matrix.postRotate(90);

    Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
    bmp.recycle();
    return resizedBitmap;
  }

  @Override
  public void onClick(View v) {
    this.finish();
//    this.finishAffinity();
//    System.exit(0);
  }

  void saveImageAndFinish(View v) {
    if (v == saveButton) {
      File cfileDir = new File("sdcard/qoontree");
      cfileDir.mkdir();
      String sdcardPath = "sdcard/qoontree/" + dateString + ".jpg";

      File cfile = new File(sdcardPath);

      try {
        FileOutputStream fos = new FileOutputStream(cfile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      Uri uri = Uri.parse("file://" + sdcardPath);
      intent.setData(uri);
      sendBroadcast(intent);

      preview.putExtra("key", distance + ";" + sdcardPath);
      this.setResult(RESULT_OK, preview);
      this.finish();
    }
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    final int Y = (int) event.getRawY();
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        _yDelta = Y - lParams.topMargin;
        break;
      case MotionEvent.ACTION_UP:
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        break;
      case MotionEvent.ACTION_POINTER_UP:
        break;
      case MotionEvent.ACTION_MOVE:
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view
            .getLayoutParams();
        layoutParams.topMargin = Y - _yDelta;
        if (view == slider1)
          sliderY[0] = layoutParams.topMargin;
        else if (view == slider2)
          sliderY[1] = layoutParams.topMargin;
        else if (view == markerSlider1)
          markerY[0] = layoutParams.topMargin;
        else if (view == markerSlider2)
          markerY[1] = layoutParams.topMargin;
        view.setLayoutParams(layoutParams);
        break;
    }

    _updatePosition();

    _frameLayout.invalidate();

    _updateMeasurement();

    return true;
  }

  void _updateMeasurement() {
    if (isMarkerRecognized == false) {
      if (markerEdit.getText().toString().length() < 1) {
        markerDistance = 0;
      } else {
        markerDistance = Double.valueOf(markerEdit.getText().toString());
      }
      distance = DistanceCalulate(1, 1, markerY[0], markerY[1], 1, 1, sliderY[0], sliderY[1]);
    } else {
      distance = DistanceCalulate(MarkerDrawView.markerX[0], MarkerDrawView.markerX[1],
          MarkerDrawView.markerY[0], MarkerDrawView.markerY[1], 1, 1, sliderY[0], sliderY[1]);
    }
    heightText.setText(new DecimalFormat("#.##").format(distance));
  }

  void _updatePosition() {
    int[] array = new int[2];
    slider1.getLocationOnScreen(array);
    sliderY[0] = array[1];
    slider2.getLocationOnScreen(array);
    sliderY[1] = array[1];
    markerSlider1.getLocationOnScreen(array);
    markerY[0] = array[1];
    markerSlider2.getLocationOnScreen(array);
    markerY[1] = array[1];
  }

  public double DistanceCalulate(double x1, double x2, double y1, double y2,
                                 double ux1, double ux2, double uy1, double uy2) {
    double dx = x1 - x2;
    double dy = y1 - y2;
    double MarkerVirtualDistance = Math.sqrt(dx * dx + dy * dy);
    double UserDx = ux1 - ux2;
    double UserDy = uy1 - uy2;
    double UserVirtualDistance = Math.sqrt(UserDx * UserDx + UserDy * UserDy);
    if (isMarkerRecognized == false)
      return (UserVirtualDistance * markerDistance) / MarkerVirtualDistance;
    else
      return (UserVirtualDistance * MainActivity.getMarkerDistance()) / MarkerVirtualDistance;
  }

  @Override
  protected void onDestroy() {
//    android.os.Process.killProcess(android.os.Process.myPid());
    super.onDestroy();
  }

}
