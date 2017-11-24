LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -llog

LOCAL_MODULE    := app
LOCAL_SRC_FILES := app.c

include $(BUILD_SHARED_LIBRARY)