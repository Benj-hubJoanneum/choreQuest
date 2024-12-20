package com.example.chorequest.repositories
import com.example.chorequest.service.server.RetrofitServiceBuilder
import com.example.chorequest.service.server.interfaces.ImageApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class ImageRepository {

    private val imageApiService = RetrofitServiceBuilder.createService(ImageApiService::class.java)

    private external fun compressImageNative(inputImage: ByteArray): ByteArray

    suspend fun uploadImage(imagePart: MultipartBody.Part): Response<Void> {
        val requestBody = imagePart.body
        val buffer = okio.Buffer()
        requestBody.writeTo(buffer)
        val originalImageBytes = buffer.readByteArray()

        object {
            init {
                System.loadLibrary("chorequest")
            }
        }

        val compressedImageBytes = compressImageNative(originalImageBytes)

        val headers = imagePart.headers
        val contentDisposition = headers?.get("Content-Disposition")
        val filename = contentDisposition?.substringAfter("filename=")?.trim('"') ?: "compressed_image.jpg"

        val compressedRequestBody = compressedImageBytes.toRequestBody(requestBody.contentType())

        val compressedPart = MultipartBody.Part.createFormData(
            name = "image",
            filename = filename,
            body = compressedRequestBody
        )

        return imageApiService.uploadImage(compressedPart)
    }
}
