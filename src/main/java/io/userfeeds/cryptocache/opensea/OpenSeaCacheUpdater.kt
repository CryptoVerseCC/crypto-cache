package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OpenSeaCacheUpdater(
        private val cache: OpenSeaCache,
        private val repository: OpenSeaRepository,
        private val service: OpenSeaService
) {

    @Scheduled(initialDelay = 3600 * 1000, fixedDelay = 8 * 3600 * 1000)
    fun updateCache() {
        val newItems = repository.findAll()
                .map(OpenSeaData::context)
                .toObservable()
                .buffer(25)
                .concatMap {
                    it.toObservable().flatMap(service::loadData)
                }
                .toList()
                .blockingGet()
        cache.update(newItems)
        repository.saveAll(newItems)
    }
}
