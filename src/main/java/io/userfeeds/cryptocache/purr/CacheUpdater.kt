package io.userfeeds.cryptocache.purr

import io.userfeeds.cryptocache.apiBaseUrl
import io.userfeeds.cryptocache.logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Component
class CacheUpdater(private val store: Store) {

    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(apiBaseUrl)
            .client(OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor())
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build())
            .build()
            .create(CryptoPurrApi::class.java)

    @Scheduled(fixedDelay = 5_000)
    fun updateCache() {
        store.cache = api.getPurrs().blockingFirst().items
        logger.info("Update cache ${javaClass.simpleName}")
    }
}
