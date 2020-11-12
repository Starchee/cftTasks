package com.starchee.retrofit

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ImgurApi {

    @Multipart
    @POST("image")
    fun uploadImage(
        @Header("Authorization") clientId: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part body: MultipartBody.Part
    ): Single<UploadImageResponse>
}

