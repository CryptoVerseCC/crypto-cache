package io.userfeeds.cryptocache.opensea

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
            logger.warn("Bad amount: ${bad.size}\n${bad.joinToString(separator = "\n")}")
        }
        val newItems = service.loadDataMultiple(existing.map(OpenSeaData::context))
                .blockingGet()
        val newItemsWithoutFails = newItems.filter { it.externalLink != "https://tokntalk.club/404" }
        cache.update(newItemsWithoutFails)
        repository.saveAll(newItemsWithoutFails)
        logger.info("OpenSea cache update: all=${newItems.size}, bad=${newItems.size - newItemsWithoutFails.size}")
    }

    @Scheduled(fixedDelay = 60 * 1000)
    fun updateBadCache() {
        val badStuff = repository.findByExternalLink("https://tokntalk.club/404")
        val newBadStuff = badStuff.filter { failsMap[it.context]?.get() ?: 0 < 10 }
        if (newBadStuff.isNotEmpty()) {
            logger.warn("New bad stuff: ${newBadStuff.size}\n${newBadStuff.joinToString(separator = "\n")}")
        }
        val newItems = service.loadDataMultiple(newBadStuff.map(OpenSeaData::context))
                .blockingGet()
        val (newItemsWithoutFails, fails) = newItems.partition { it.externalLink != "https://tokntalk.club/404" }
        cache.update(newItemsWithoutFails)
        repository.saveAll(newItemsWithoutFails)
        fails.forEach {
            failsMap[it.context] = failsMap.getOrDefault(it.context, AtomicInteger()).apply { incrementAndGet() }
        }
    }
}
