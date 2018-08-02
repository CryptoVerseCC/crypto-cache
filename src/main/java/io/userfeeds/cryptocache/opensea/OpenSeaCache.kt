package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class OpenSeaCache(
        private val openSeaRepository: OpenSeaRepository,
        private val service: OpenSeaService
) {

    private val cache = ConcurrentHashMap<Asset, Observable<OpenSeaData>>()

    fun asset(context: String): Observable<OpenSeaData> {
        val asset: Asset = context.substringAfter(":")
                .split(":")
                .let { (address, token) -> Asset(address, token) }
        return cache.getOrPut(asset) {
            getAssetFromPersistentCache(asset).orElse(getAssetFromApi(asset))
        }
    }

    private fun getAssetFromPersistentCache(asset: Asset): Optional<Observable<OpenSeaData>> {
        return openSeaRepository.findById(asset)
                .map { Observable.just(it) }
    }

    private fun getAssetFromApi(asset: Asset): Observable<OpenSeaData> {
        return service.asset(asset.address, asset.token)
                .map {
                    OpenSeaData(
                            asset = asset,
                            backgroundColor = it.backgroundColor,
                            imageUrl = it.imageUrl,
                            name = it.name
                    )
                }
                .doOnNext {
                    cache[asset] = Observable.just(it)
                    openSeaRepository.save(it)
                }
                .share()
    }

    fun invalidate() {
        cache.clear()
    }
}