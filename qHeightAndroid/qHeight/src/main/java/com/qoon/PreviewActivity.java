package com.qoon;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class PreviewActivity extends Activity implements SurfaceHolder.Callback {

  Camera camera;
  Size previewSize;
  SurfaceView surfaceView;
  SurfaceHolder surfaceHolder;
  ImageThread _thread;
  Handler _handler = new Handler(Looper.getMainLooper());

  private int screenWidth, screenHeight;
  private LayoutParams layoutParamsControl;
  static public boolean bProcessing = false;
  boolean previewing = false;
  LayoutInflater controlInflater = null;
  private MarkerDrawView markerDrawView;
  private static Bitmap bitmap;
  Intent parentIntent;
  SensorView sensorView;

//  RecogView recogView;
  private RecogView markerRecogView;
  private RecogView sensorRecogView;
  MediaPlayer _shootMP;
  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

  public static Bitmap getBitmap() {
    return bitmap;
  }

  AudioManager am;
  Button takePicture;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    _shootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));

    setContentView(R.layout.preview);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    parentIntent = getIntent();
    View view = (View) findViewById(R.id.control);
    sensorView = (SensorView) view.findViewById(R.id.qoonSensorView);
    surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
    surfaceHolder = surfaceView.getHolder();
    surfaceHolder.addCallback(this);
    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    controlInflater = LayoutInflater.from(getBaseContext());
    layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

    markerDrawView = new MarkerDrawView(this);

    markerRecogView = (RecogView) view.findViewById(R.id.markerRecogView);
    sensorRecogView = (RecogView) view.findViewById(R.id.sensorRecogView);
    sensorView.setFlagParent(sensorRecogView);
    markerDrawView.setFlagParent(markerRecogView);
    addContentView(markerDrawView, layoutParamsControl);
    markerRecogView.setVisibility(View.GONE);

    takePicture = (Button) view.findViewById(R.id.takePicture);
    takePicture.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        takePicture.setEnabled(false);
        Toast.makeText(getBaseContext(), "Image Processing...", Toast.LENGTH_SHORT).show();
        _handler.post(_runnable);
        camera.takePicture(null, null, mPictureCallbackJpeg);
      }
    });

    Button exit = (Button) findViewById(R.id.exitPreview);
    exit.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
      }
    });

    _initGuideButton();

    surfaceView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        camera.autoFocus(new AutoFocusCallback() {
          public void onAutoFocus(boolean success, Camera camera) {

          }
        });
      }
    });

    _thread = new ImageThread(surfaceHolder, this);
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

  @Override
  protected void onResume() {
    takePicture.setEnabled(true);
    super.onResume();
  }


  private Runnable _runnable = new Runnable() {
    @Override
    public void run() {
      int vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
      am.setStreamVolume(
          AudioManager.STREAM_MUSIC,
          am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1,
          AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
      _shootMP.setVolume(1.0f, 1.0f);
      _shootMP.start();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      am.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

    }
  };
  Camera.PictureCallback mPictureCallbackJpeg = new Camera.PictureCallback() {
    public void onPictureTaken(byte[] data, Camera c) {
      long x = System.currentTimeMillis();
      int width = c.getParameters().getPictureSize().width;
      int height = c.getParameters().getPictureSize().height;

      camera.setPreviewCallback(null);
      camera.stopPreview();
      camera.release();
      camera = null;
      previewing = false;

      bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

      byte[] b = data;
      try {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;
        bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, opts);
      } catch (Exception e) {
        return;
      }
      long y = System.currentTimeMillis();
      Log.i("JHR", "time : " + String.valueOf(y - x));
      Intent it = new Intent(getBaseContext(), MeasureActivity.class);
      startActivityForResult(it, 1);

      //			MeasureHeight drawBoard = new MeasureHeight(getBaseContext(), previewSize.width, previewSize.height);
      //			addContentView(drawBoard, new LayoutParams(previewSize.width, previewSize.height));
    }
  };

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i("abc", "PreviewActivity " + resultCode);
    if (requestCode == 1 && data != null && resultCode == RESULT_OK) {
      parentIntent.putExtra("key", data.getStringExtra("key"));
      this.setResult(RESULT_OK, parentIntent);
      this.finish();
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width,
                             int height) {

    Camera.Parameters params = camera.getParameters();
    List<Size> arSize = params.getSupportedPreviewSizes();
    if (arSize == null) {
      params.setPreviewSize(width, height);
    } else {
      int diff = 10000;
      Size opti = null;
      for (Size s : arSize) {
        if (Math.abs(s.height - height) < diff) {
          diff = Math.abs(s.height - height);
          opti = s;
        }
      }
      params.setPreviewSize(opti.width, opti.height);
    }

    Camera.Parameters parameters = camera.getParameters();
    previewSize = camera.getParameters().getPreviewSize();
    screenWidth = previewSize.width;
    screenHeight = previewSize.height;
    //		parameters.set("orientation", "portrait");
    //		parameters.set("rotation", 90);
    camera.setParameters(parameters);
    camera.startPreview();
    camera.setDisplayOrientation(90);
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    if (camera != null) {
      camera.release();
      camera = null;
    }

    try {
      camera = Camera.open();
      camera.setPreviewDisplay(surfaceHolder);

//			DisplayMetrics metrics = new DisplayMetrics();
//			getWindowManager().getDefaultDisplay().getMetrics(metrics);
//			screenWidth = metrics.widthPixels;
//			screenHeight = metrics.heightPixels;
//
//			// �Ʒ� ���ڸ� �����Ͽ� �ڽ��� ���ϴ� �ػ󵵷� �����Ѵ�
//
//			Camera.Parameters params = camera.getParameters();
//			params.setPreviewSize(screenWidth, screenHeight);
//
//			// screen ũ�⿡ �� �������!!!!!!!!!!!!!!!!!!!!!
//			params.setPictureSize(screenWidth, screenHeight);
//			//	camera.setParameters(params);
      camera.startPreview();


    } catch (IOException e) {

      camera.release();
      camera = null;

    }

    camera.setPreviewCallback(new PreviewCallback() {

      @Override
      public void onPreviewFrame(byte[] data, Camera camera) {
        if (!bProcessing) {
          bProcessing = true;
          _thread.setRunning(true);
          _thread.setThread(data, screenWidth, screenHeight, markerDrawView);
          _handler.post(_thread);
          markerDrawView.invalidate();
          //				  	ImageView view = new ImageView(getBaseContext());
        }
      }
    });
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    if (camera != null) {
      camera.setPreviewCallback(null);
      camera.stopPreview();
      camera.release();
      camera = null;
      previewing = false;
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      boolean retry = true;
      while (retry) {
        try {
          _thread.setRunning(false);
          _thread.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        retry = false;
      }
      super.finish();
      return false;
    }
    return super.onKeyDown(keyCode, event);
  }

  public int getSurfaceViewWidth() {
    return surfaceView.getWidth();
  }

  public int getSurfaceViewHeight() {
    return surfaceView.getHeight();
  }

  @Override
  public void onStart() {
    super.onStart();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    Action viewAction = Action.newAction(
        Action.TYPE_VIEW, // TODO: choose an action type.
        "PreviewActivity Page", // TODO: Define a title for the content shown.
        // TODO: If you have web page content that matches this app activity's content,
        // make sure this auto-generated web page URL is correct.
        // Otherwise, set the URL to null.
        Uri.parse("http://host/path"),
        // TODO: Make sure this auto-generated app deep link URI is correct.
        Uri.parse("android-app://com.qoon/http/host/path")
    );
    AppIndex.AppIndexApi.start(client, viewAction);
  }

  @Override
  public void onStop() {
    super.onStop();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    Action viewAction = Action.newAction(
        Action.TYPE_VIEW, // TODO: choose an action type.
        "PreviewActivity Page", // TODO: Define a title for the content shown.
        // TODO: If you have web page content that matches this app activity's content,
        // make sure this auto-generated web page URL is correct.
        // Otherwise, set the URL to null.
        Uri.parse("http://host/path"),
        // TODO: Make sure this auto-generated app deep link URI is correct.
        Uri.parse("android-app://com.qoon/http/host/path")
    );
    AppIndex.AppIndexApi.end(client, viewAction);
    client.disconnect();
  }
}