package com.starchee.retrofit

import android.content.res.Resources
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class RetrofitRepository private constructor(
    resources: Resources
) {

    private val clientId = resources.getString(R.string.Client_ID)

    companion object {
        private var instance: RetrofitRepository? = null
        private var retrofitService: ImgurApi? = null
        private const val BASE_URL = "https://api.imgur.com/3/"


        fun getInstance(resources: Resources): RetrofitRepository {
            instance ?: build(resources)
            return instance!!
        }

        private fun build(resources: Resources) {
            buildRetrofit()
            instance = RetrofitRepository(resources)
        }

        private fun buildRetrofit() {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(buildOkHttpClient())
                .build()
            retrofitService = retrofit.create(ImgurApi::class.java)
        }

        private fun buildOkHttpClient() = OkHttpClient.Builder()
            .cache(null)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    }

    fun uploadImage(title: String, description: String, image: File): Single<UploadImageResponse> {
        val body = MultipartBody.Part.createFormData(
            "image",
            image.name,
            image.asRequestBody("image/*".toMediaTypeOrNull()),
        )

       return retrofitService!!.uploadImage(
            clientId,
            title,
            description,
            body
        )
    }
}