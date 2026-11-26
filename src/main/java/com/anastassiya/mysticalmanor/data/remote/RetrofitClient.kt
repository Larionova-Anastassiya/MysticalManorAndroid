
package com.anastassiya.mysticalmanor.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL =
        "https://www.dnd5eapi.co/api/2014/"

    private val loggingInterceptor =
        HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { message ->
                Log.d("BestiaryAPI", message)
            }
        ).apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val api: BestiaryApi by lazy {
        retrofit.create(BestiaryApi::class.java)
    }
}
