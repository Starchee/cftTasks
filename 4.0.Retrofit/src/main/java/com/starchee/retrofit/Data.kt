package com.starchee.retrofit

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("link")
    val link: String
)