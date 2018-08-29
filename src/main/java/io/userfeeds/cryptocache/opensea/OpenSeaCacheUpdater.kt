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

    @Scheduled(initialDelay = 60 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    fun updateCache() {
        val existing = repository.findAll()
        val newItems = existing
                .map(OpenSeaData::context)
                .toObservable()
                .buffer(2)
                .concatMap {
                    it.toObservable().flatMap(service::loadData)
                }
                .toList()
                .blockingGet()
        val newItemsWithoutFails = newItems.filter { it.externalLink != "https://tokntalk.club/404" }
        cache.update(newItemsWithoutFails)
        repository.saveAll(newItemsWithoutFails)
    }
}
