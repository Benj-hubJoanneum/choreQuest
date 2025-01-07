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

class ImageRepository {

    private val imageApiService by lazy {
        RetrofitServiceBuilder.createService(ImageApiService::class.java)
    }

    companion object {
        init {
            System.loadLibrary("chorequest")
        }
    }

    private external fun compressImageNative(inputImage: ByteArray): ByteArray

    suspend fun uploadImage(imagePart: MultipartBody.Part): Response<Void> {
        val originalImageBytes = extractBytesFromRequestBody(imagePart.body)
        val compressedImageBytes = compressImageNative(originalImageBytes)

        val compressedRequestBody = compressedImageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val compressedPart = MultipartBody.Part.createFormData(
            name = "image",
            filename = extractFilenameFromHeaders(imagePart.headers) ?: "compressed_image.jpg",
            body = compressedRequestBody
        )

        return imageApiService.uploadImage(compressedPart)
    }

    fun buildImageUri(fileName: String): String {
        return "${Constants.BASE_URL}/images/$fileName"
    }

    fun prepareImagePart(bitmap: Bitmap, fileName: String): MultipartBody.Part {
        val byteArray = bitmap.toByteArray()
        val compressedBytes = compressImageNative(byteArray) // Apply native compression here
        val requestBody = compressedBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", fileName, requestBody)
    }

    private fun extractBytesFromRequestBody(requestBody: okhttp3.RequestBody): ByteArray {
        val buffer = okio.Buffer()
        requestBody.writeTo(buffer)
        return buffer.readByteArray()
    }

    private fun extractFilenameFromHeaders(headers: okhttp3.Headers?): String? {
        return headers?.get("Content-Disposition")
            ?.substringAfter("filename=")
            ?.trim('"')
    }

    private fun Bitmap.toByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }
}
