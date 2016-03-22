LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include /Users/shhong/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

#LOCAL_C_INCLUDES += /Users/shhong/OpenCV-android-sdk/sdk/native/jni/include

LOCAL_LDLIBS += -llog -ldl
LOCAL_MODULE := test3
LOCAL_SRC_FILES := test3.cpp
include $(BUILD_SHARED_LIBRARY)