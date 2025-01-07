// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("chorequest");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("chorequest")
//      }
//    }

#include <jni.h>
#include <string.h>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_chorequest_repositories_ImageRepository_compressImageNative(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jbyteArray input_image) {

    return input_image;
}