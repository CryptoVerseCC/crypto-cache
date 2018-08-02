package io.userfeeds.cryptocache

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

fun apiRetrofit(): Retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(apiBaseUrl)
        .client(OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .build())
        .build()