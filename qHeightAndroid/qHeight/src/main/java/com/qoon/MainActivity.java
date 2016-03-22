package com.qoon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

  public Intent parentAppIntent;
  public Intent preview;
  private static boolean isStart = false;
  private static double MarkerRealDistance = 15;
  private static Context context;
  private static float scale;

  public static double getMarkerDistance() {
    return MarkerRealDistance;
  }

  public static float pixelFromDP(float DP) {
    return (DP / scale * 1.5f);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    context = this.getBaseContext();
    //Toast.makeText(MainActivity.this, "Hellohoho2", Toast.LENGTH_SHORT).show();
    scale = context.getResources().getDisplayMetrics().density;
    if (isStart == false) {
      isStart = true;
//      try {
//        InputStream markerStream = getResources().openRawResource(R.drawable.marker2);
//        File dir = new File("sdcard/test");
//        dir.mkdirs();
//        File f = new File("sdcard/test/marker.jpg");
//        OutputStream out = new FileOutputStream(f);
//        byte buf[] = new byte[1024];
//        int len;
//        while ((len = markerStream.read(buf)) > 0) {
//          out.write(buf, 0, len);
//        }
//        out.close();
//        markerStream.close();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
    }
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    parentAppIntent = getIntent();
    preview = new Intent(this, PreviewActivity.class);
    preview.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
    startActivityForResult(preview, 1);
    ImageButton picture = (ImageButton) findViewById(R.id.btn);
    picture.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1 && data != null && resultCode == RESULT_OK) {
      parentAppIntent.putExtra("key", data.getStringExtra("key"));
      this.setResult(RESULT_OK, parentAppIntent);
      this.finish();
    }
    super.onActivityResult(requestCode, resultCode, data);
    this.finish();
  }
}