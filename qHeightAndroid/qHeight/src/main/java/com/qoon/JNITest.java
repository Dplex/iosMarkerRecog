package com.qoon;

public class JNITest {

  public native boolean test3(int width, int height,
                              byte[] NV21FrameData, int[] pixels);

  static {
    System.loadLibrary("test3");
    System.loadLibrary("opencv_java3");
  }
}
