package io.userfeeds.cryptocache.opensea

import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.Executors

@Component
class OpenSeaService {

    private val api = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.from(Executors.newFixedThreadPool(10))))
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }).build())
            .baseUrl("https://opensea-api.herokuapp.com/")
            .build()
            .create(OpenSeaApi::class.java)

    fun data(asset: Asset) = api.asset(asset.address, asset.token)
            .map {
                OpenSeaData(
                        asset = asset,
                        backgroundColor = it.backgroundColor,
                        externalLink = it.externalLink?.takeIf { it.contains(asset.token) }
                                ?: "https://opensea.io/assets/${asset.address}/${asset.token}",
                        imageUrl = it.imageUrl,
                        name = it.name
                )
            }
}
