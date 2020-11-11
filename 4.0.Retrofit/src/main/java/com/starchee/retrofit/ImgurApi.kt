package com.starchee.retrofit

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface ImgurApi {

    @Multipart
    @POST("image")
    fun uploadImage(
        @Header("Authorization") clientId: String,
        @Query("title") title: String,
        @Query("description") description: String,
        @Part body: MultipartBody.Part): Single<UploadImageResponse>
}

