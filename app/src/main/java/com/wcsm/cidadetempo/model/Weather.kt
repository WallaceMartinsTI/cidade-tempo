package com.wcsm.cidadetempo.model

data class Weather(
    val cod: Int,
    val dt: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val weather: List<WeatherX>,
    val wind: Wind
)