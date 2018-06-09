package io.userfeeds

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Component
class CacheUpdater(private val store: Store) {

    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl("https://api.userfeeds.io/ranking/")
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor()).build())
            .build()
            .create(CryptoPurrApi::class.java)

    @Scheduled(fixedDelay = 60_000)
    fun updateCache() {
        val response = api.getPurrs().blockingFirst().items
        store.cache = response
        println("Cache updated! ${System.currentTimeMillis()}")
    }
}