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

    @Scheduled(fixedDelay = 8 * 3600 * 1000)
    fun updateCache() {
        val newItems = repository.findAll()
                .map(OpenSeaData::asset)
                .toObservable()
                .buffer(25)
                .concatMap {
                    it.toObservable().flatMap { asset ->
                        service.asset(asset.address, asset.token).map {
                            OpenSeaData(
                                    asset = asset,
                                    backgroundColor = it.backgroundColor,
                                    imageUrl = it.imageUrl,
                                    name = it.name
                            )
                        }
                    }
                }
                .toList()
                .blockingGet()
        repository.saveAll(newItems)
        cache.invalidate()
    }
}