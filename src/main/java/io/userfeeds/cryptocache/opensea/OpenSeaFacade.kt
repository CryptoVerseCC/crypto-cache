package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

@Component
class OpenSeaFacade {

    private val cache = ConcurrentHashMap<Asset, Observable<OpenSeaData>>()
    private val api = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.from(Executors.newFixedThreadPool(10))))
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }).build())
            .baseUrl("https://opensea-api.herokuapp.com/")
            .build()
            .create(OpenSeaApi::class.java)

    fun asset(context: String): Observable<OpenSeaData> {
        val asset: Asset = context.substringAfter(":")
                .split(":")
                .let { (address, token) -> address to token }
        return cache.getOrPut(asset) {
            api.asset(asset.address, asset.token)
                    .map {
                        OpenSeaData(
                                backgroundColor = it.backgroundColor,
                                imageUrl = it.imageUrl,
                                name = it.assetContract.name,
                                owner = it.owner.address
                        )
                    }
                    .doOnNext { cache[asset] = Observable.just(it) }
        }
    }
}

typealias Asset = Pair<String, String>

val Asset.address
    get() = this.first

val Asset.token
    get() = this.second

data class OpenSeaData(
        val backgroundColor: String?,
        val owner: String,
        val imageUrl: String,
        val name: String
)