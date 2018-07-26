package io.userfeeds.cryptocache.cryptoverse_discovery

import io.userfeeds.cryptocache.logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Component
class DiscoveryCacheUpdater(private val repository: DiscoveryRepository) {

    private val api by lazy {
        Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("https://api.userfeeds.io/ranking/")
                .client(OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HEADERS))
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build())
                .build()
                .create(DiscoveryApi::class.java)
    }

    @Scheduled(fixedDelay = 300_000)
    fun updateCache() {
        val assets = repository.assets.toList()
        assets.forEach(this::updateForAsset)
        logger.info("Update cache ${assets.size} ${javaClass.simpleName}")
    }

    fun updateForAsset(asset: String) {
        try {
            val latest = api.latestPurrers(asset).blockingFirst().items
            val twitter = api.socialProfiles("twitter", asset).blockingFirst().items
            val facebook = api.socialProfiles("facebook", asset).blockingFirst().items
            val instagram = api.socialProfiles("instagram", asset).blockingFirst().items
            val github = api.socialProfiles("github", asset).blockingFirst().items
            repository.put(asset, Discovery(latest, twitter, facebook, instagram, github))
            logger.info("Update cache: $asset ${javaClass.simpleName}")
        } catch (exception: Throwable) {
            logger.warn("Exception during cache update: $asset ${javaClass.simpleName}", exception)
        }
    }
}
