package com.wcsm.cidadetempo.api

import okhttp3.Interceptor
import okhttp3.Response

class ParamsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request()
        val actualUrl = requestBuilder.url()
        val newUrl = actualUrl.newBuilder()
            .addQueryParameter(
                "units", "metric"
            ).addQueryParameter(
                "lang", "pt_br"
            ).addQueryParameter(
                "appid", "a50e8c8f395d107c8b239712e78b2a69"
            ).build()

        val request = requestBuilder.newBuilder().url(newUrl).build()

        return chain.proceed(request)
    }
}