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
        return service.data(asset)
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