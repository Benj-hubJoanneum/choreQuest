package com.example.chorequest.repositories

import android.graphics.Bitmap
import com.example.chorequest.service.server.Constants
import com.example.chorequest.service.server.RetrofitServiceBuilder
import com.example.chorequest.service.server.interfaces.ImageApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import kotlin.coroutines.Continuation

class ImageRepository {

    private val imageApiService by lazy {
        RetrofitServiceBuilder.createService(ImageApiService::class.java)
    }

    companion object {
        init {
            System.loadLibrary("chorequest")
        }
    }

    // External native function to compress the image
    private external fun compressImageNative(inputImage: ByteArray): ByteArray

    /**
     * Uploads an image with compression applied using a native method.
     *
     * @param imagePart Multipart body part representing the image to be uploaded.
     * @return Response from the server.
     */
    suspend fun uploadImage(imagePart: MultipartBody.Part): Response<Void> {
        val originalImageBytes = extractRequestBodyBytes(imagePart.body)
        val compressedImageBytes = compressImageNative(originalImageBytes)

        val filename = extractFilenameFromHeaders(imagePart.headers) ?: "compressed_image.jpg"

        val compressedRequestBody = compressedImageBytes.toRequestBody(imagePart.body.contentType())
        val compressedPart = MultipartBody.Part.createFormData(
            name = "image",
            filename = filename,
            body = compressedRequestBody
        )

        return imageApiService.uploadImage(compressedPart)
    }

    fun buildImageUri(fileName: String): String {
        val baseUri = Constants.BASE_URL
        return "$baseUri/images/$fileName"
    }

    /**
     * Prepares a MultipartBody.Part from a Bitmap and file name.
     *
     * @param bitmap Bitmap to be converted into a byte array.
     * @param fileName Name of the file to be used in the multipart part.
     * @return MultipartBody.Part representing the image.
     */
    fun prepareImagePart(bitmap: Bitmap, fileName: String): MultipartBody.Part {
        val byteArray = bitmap.toCompressedByteArray()
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", fileName, requestBody)
    }

    /**
     * Extracts the raw byte array from a RequestBody.
     *
     * @param requestBody RequestBody to extract bytes from.
     * @return Byte array representation of the RequestBody content.
     */
    private fun extractRequestBodyBytes(requestBody: okhttp3.RequestBody): ByteArray {
        val buffer = okio.Buffer()
        requestBody.writeTo(buffer)
        return buffer.readByteArray()
    }

    /**
     * Extracts the filename from the headers of a MultipartBody.Part.
     *
     * @param headers Headers of the multipart part.
     * @return Extracted filename or null if not found.
     */
    private fun extractFilenameFromHeaders(headers: okhttp3.Headers?): String? {
        return headers?.get("Content-Disposition")
            ?.substringAfter("filename=")
            ?.trim('"')
    }

    /**
     * Extension function to compress a Bitmap into a byte array.
     *
     * @param format Format for compression (JPEG is used by default).
     * @param quality Quality for compression (default is 100).
     * @return Compressed byte array.
     */
    private fun Bitmap.toCompressedByteArray(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()
        this.compress(format, quality, outputStream)
        return outputStream.toByteArray()
    }
}
