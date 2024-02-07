package com.wcsm.cidadetempo.api

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ParamsInterceptor())
        .build()

    val weatherAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(WeatherAPI::class.java)
    }
}