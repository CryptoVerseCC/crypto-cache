package io.userfeeds.cryptocache.cryptoverse

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.UnsupportedOperationException

@RestController
class CryptoverseFeedController(private val repository: CryptoverseFeedRepository) {

    @RequestMapping("/cryptoverse_feed")
    fun getFeed(
            @RequestParam("oldestKnown", required = false) oldestKnown: String?,
            @RequestParam("lastVersion", required = false) lastVersion: Long?,
            @RequestParam("size", required = false) size: Int?): Page {
        val cache = repository.cache
        var version: Long? = cache.version
        val items = if (oldestKnown != null && lastVersion != null) {
            val almost = cache.allItems.takeWhile { it["id"] != oldestKnown }
            val unfiltered = almost + cache.allItems[almost.size]
            unfiltered.filter { it["version"] as Long > lastVersion }
        } else if (oldestKnown != null && size != null) {
            version = null
            cache.allItems.dropWhile { it["id"] != oldestKnown }.drop(1).take(size)
        } else if (size != null) {
            cache.allItems.take(size)
        } else {
            throw UnsupportedOperationException("oldestKnown+lastVersion, oldestKnown+size or size missing")
        }
        return Page(items = items, total = cache.allItems.size, version = version)
    }
}

inline fun <T> T.runIf(condition: Boolean, block: T.() -> T): T {
    return if (condition) block() else this
}

data class Page(val items: List<Any>, val total: Int, val version: Long?)
