package com.wcsm.cidadetempo.api

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    //https://api.openweathermap.org/data/2.5/weather?q=Betim&units=metric&lang=pt_br&appid=a50e8c8f395d107c8b239712e78b2a69

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