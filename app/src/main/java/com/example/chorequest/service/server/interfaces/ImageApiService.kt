package com.example.chorequest.service.server.interfaces

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ImageApiService {
    @Multipart
    @POST("images/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<Void>

    @Multipart
    @POST("files/upload")
    suspend fun uploadFile(@Part filePart: MultipartBody.Part): Response<Void>
}