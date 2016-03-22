package com.qoon;

import android.view.SurfaceHolder;

public class ImageThread extends Thread {

//  private SurfaceHolder myThreadSurfaceHolder;
  private boolean myThreadRun = false;
  JNITest jniCall;
//  private int imageFormat;

  private int[] pixels = null;
  private byte[] FrameData = null;
  private int PreviewSizeWidth;
  private int PreviewSizeHeight;
  private MarkerDrawView markerDrawView;
  private PreviewActivity parentActivity;

  public ImageThread(SurfaceHolder surfaceHolder, PreviewActivity surfaceView) {
//    myThreadSurfaceHolder = surfaceHolder;
    jniCall = new JNITest();
    parentActivity = surfaceView;
  }

  public void setRunning(boolean b) {
    myThreadRun = b;

  }

  public void setThread(byte buff[], int width, int height, MarkerDrawView Draw) {
    pixels = new int[10];
    FrameData = buff;
    PreviewSizeWidth = width;
    PreviewSizeHeight = height;
    markerDrawView = Draw;
  }

  @Override
  public void run() {

//    jniCall.test3(PreviewSizeWidth, PreviewSizeHeight, FrameData, pixels);
//    for (int i = 0; i < 4; i++) {
//      markerDrawView.y[i] = (float) pixels[i * 2] / (float) PreviewSizeWidth;
//      markerDrawView.x[i] = ((float) PreviewSizeHeight - (float) pixels[i * 2 + 1]) / (float) PreviewSizeHeight;
//    }
    PreviewActivity.bProcessing = false;
  }
}