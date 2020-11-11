package com.starchee.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UploadImageResponse(

    @SerializedName("success")
    @Expose
    val success: String,

    @SerializedName("status")
    @Expose
    val status: Int,

    @SerializedName("data")
    @Expose
    val data: Data
)