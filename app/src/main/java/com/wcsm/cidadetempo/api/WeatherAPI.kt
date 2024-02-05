package com.wcsm.cidadetempo.api

import com.wcsm.cidadetempo.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("weather")
    suspend fun getWeatherData(
        @Query("q") q: String
    ): Response<Weather>
}