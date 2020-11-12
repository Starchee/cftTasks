package com.starchee.retrofit

import com.google.gson.annotations.SerializedName

data class UploadImageResponse(

    @SerializedName("success")
    val success: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("data")
    val data: Data
)