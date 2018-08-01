package io.userfeeds.cryptocache.cryptoverse_discovery

import io.userfeeds.cryptocache.ContextItemContextExtractor
import io.userfeeds.cryptocache.ContextdItemDataAdder
import io.userfeeds.cryptocache.apiBaseUrl
import io.userfeeds.cryptocache.cryptoverse_discovery.Type.erc20
import io.userfeeds.cryptocache.logger
import io.userfeeds.cryptocache.opensea.OpenSeaItemInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Component
class DiscoveryCacheUpdater(private val repository: DiscoveryRepository,
                            private val openSeaItemInterceptor: OpenSeaItemInterceptor) {

    private val api by lazy {
        Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(apiBaseUrl)
                .client(OkHttpClient.Builder()
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build())
                .build()
                .create(DiscoveryApi::class.java)
    }

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        val assets = repository.assets
        assets.forEach { (asset, type) -> updateForAsset(asset, type) }
        logger.info("Update cache ${assets.size} ${javaClass.simpleName}")
    }

    fun updateForAsset(asset: String, type: Type) {
        try {
            val name = if (type == erc20) "experimental_author_balance" else "experimental_filter_origin"
            val latest = api.latestPurrers(name, asset).blockingFirst().items
            openSeaItemInterceptor.addOpenSeaData(latest, ::ContextdItemDataAdder, ContextItemContextExtractor())
            val twitter = api.socialProfiles("twitter", name, asset).blockingFirst().items
            val facebook = api.socialProfiles("facebook", name, asset).blockingFirst().items
            val instagram = api.socialProfiles("instagram", name, asset).blockingFirst().items
            val github = api.socialProfiles("github", name, asset).blockingFirst().items
            repository.put(asset, Discovery(latest, twitter, facebook, instagram, github))
            logger.info("Update cache: $asset ${javaClass.simpleName}")
        } catch (exception: Throwable) {
            logger.warn("Exception during cache update: $asset ${javaClass.simpleName}", exception)
        }
    }
}
