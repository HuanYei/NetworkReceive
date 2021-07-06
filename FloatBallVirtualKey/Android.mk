LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := FloatBallVirtualKey
LOCAL_MODULE_TAGS := optional
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_CERTIFICATE := platform

LOCAL_STATIC_JAVA_LIBRARIES := \
logging httpclient httpclient-cache httpcore httpmime gson

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_DEX_PREOPT := false

LOCAL_JAVA_LIBRARIES := \
    android-support-v4 
#LOCAL_PRIVILEGED_MODULE := true
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_JAVACFLAGS += -Xlint:all -Xlint:-path
LOCAL_JAVACFLAGS += -Xlint:-deprecation

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
logging:libs/commons-logging-1.2.jar \
httpclient:libs/httpclient-4.2.6.jar \
httpclient-cache:libs/httpclient-cache-4.2.6.jar \
httpcore:libs/httpcore-4.2.5.jar \
httpmime:libs/httpmime-4.2.6.jar \
gson:libs/gson-2.8.2.jar

LOCAL_MODULE_TAGS := optional

include $(BUILD_PACKAGE)

