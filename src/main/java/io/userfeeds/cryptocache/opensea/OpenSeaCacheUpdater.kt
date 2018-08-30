package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import io.userfeeds.cryptocache.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Component
class OpenSeaCacheUpdater(
        private val cache: OpenSeaCache,
        private val repository: OpenSeaRepository,
        private val service: OpenSeaService
) {

    val failsMap = ConcurrentHashMap<String, AtomicInteger>()

    @Scheduled(initialDelay = 60 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    fun updateCache() {
        val existing = repository.findAll()
        val bad = existing.filter { it.externalLink == "https://tokntalk.club/404" }
        if (bad.isNotEmpty()) {
            logger.warn("Failed amount: ${bad.size}\n${bad.joinToString(separator = "\n")}")
        }
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

    @Scheduled(fixedDelay = 60 * 1000)
    fun updateBadCache() {
        val badStuff = repository.findByExternalLink("https://tokntalk.club/404")
        val newBadStuff = badStuff.filter { failsMap[it.context]?.get() ?: 0 < 10 }
        if (newBadStuff.isNotEmpty()) {
            logger.warn("New bad stuff: ${newBadStuff.size}\n${newBadStuff.joinToString(separator = "\n")}")
        }
        val newItems = newBadStuff
                .map(OpenSeaData::context)
                .toObservable()
                .flatMap(service::loadData)
                .toList()
                .blockingGet()
        val (newItemsWithoutFails, fails) = newItems.partition { it.externalLink != "https://tokntalk.club/404" }
        cache.update(newItemsWithoutFails)
        repository.saveAll(newItemsWithoutFails)
        fails.forEach {
            failsMap[it.context] = failsMap.getOrDefault(it.context, AtomicInteger()).apply { incrementAndGet() }
        }
    }
}
