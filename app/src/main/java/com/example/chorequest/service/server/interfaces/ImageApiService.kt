package com.example.chorequest.service.server.interfaces

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ImageApiService {

    @GET("images/{id}")
    suspend fun downloadImage(@Path("id") imageId: String): Response<ResponseBody>

    @Multipart
    @POST("images/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<Void>
}