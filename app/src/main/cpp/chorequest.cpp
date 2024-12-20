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
    // Get the length of the input image byte array
    jsize inputLength = (*env).GetArrayLength(input_image);

    // Access the input image bytes
    jbyte *inputBytes = (*env).GetByteArrayElements(input_image, nullptr);

    // Allocate a new byte array for the compressed image (reduce size by half)
    jsize compressedLength = inputLength / 2;
    jbyteArray compressedImage = (*env).NewByteArray(compressedLength);

    // Compress the image by copying every other byte
    for (jsize i = 0, j = 0; i < inputLength; i += 2, j++)
        (*env).SetByteArrayRegion( compressedImage, j, 1, &inputBytes[i]);


    // Release the input image byte array
    (*env).ReleaseByteArrayElements(input_image, inputBytes, 0);

    // Return the compressed image byte array
    return compressedImage;
}