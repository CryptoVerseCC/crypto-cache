package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import org.springframework.stereotype.Component

@Component
class OpenSeaCacheUpdater(
        private val cache: OpenSeaCache,
        private val repository: OpenSeaRepository,
        private val service: OpenSeaService
) {

    //@Scheduled(fixedDelay = 8 * 3600 * 1000, initialDelay = 3600 * 1000)
    fun updateCache() {
        val newItems = repository.findAll()
                .map(OpenSeaData::asset)
                .toObservable()
                .buffer(25)
                .concatMap {
                    it.toObservable().flatMap { asset ->
                        service.data(asset)
                    }
                }
                .toList()
                .blockingGet()
        repository.saveAll(newItems)
        cache.invalidate()
    }
}