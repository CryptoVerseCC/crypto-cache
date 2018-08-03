package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class OpenSeaCache(
        private val openSeaRepository: OpenSeaRepository,
        private val service: OpenSeaService
) {

    private val cache = ConcurrentHashMap<Asset, Observable<OpenSeaData>>()
    private val cacheInit = initializeCache()
    private val logger = LoggerFactory.getLogger(OpenSeaCache::class.java)

    fun asset(context: String): Observable<OpenSeaData> {
        val asset: Asset = context.substringAfter(":")
                .split(":")
                .let { (address, token) -> Asset(address, token) }
        return cacheInit.flatMap {
            cache.getOrPut(asset) { getAssetFromApi(asset) }
        }
    }

    private fun initializeCache(): Observable<Unit> {
        return Observable
                .fromCallable {
                    logger.warn("Cache initialization started!")
                    val items = openSeaRepository.findAll()
                    cache.putAll(items.map { it.asset to Observable.just(it) }.toMap())
                }
                .cache()
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