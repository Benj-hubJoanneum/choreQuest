package com.example.chorequest.repositories

import com.example.chorequest.service.server.interfaces.ImageApiService
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class ImageRepository(private val api: ImageApiService) {
    suspend fun downloadImage(imageId: String): Response<ResponseBody> {
        return api.downloadImage(imageId)
    }

    suspend fun uploadImage(image: MultipartBody.Part): Response<Void> {
        return api.uploadImage(image)
    }
}