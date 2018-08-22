package io.userfeeds.cryptocache.cryptoverse.club.common

import io.userfeeds.cryptocache.cryptoverse.main.common.Page
import io.userfeeds.cryptocache.id
import io.userfeeds.cryptocache.version

object Controller {

    fun getFeed(repository: Repository, id: String, oldestKnown: String?, lastVersion: Long?, size: Int?): Page {
        check(repository.cache.containsKey(id)) { "API is not ready." }
        val cache = repository.cache.getValue(id)
        var version: Long? = cache.version
        val items = if (oldestKnown != null && lastVersion != null) {
            check(size == null)
            val almost = cache.allItems.takeWhile { it.id != oldestKnown }
            val unfiltered = almost + if (almost.size < cache.allItems.size) cache.allItems[almost.size] else mutableMapOf()
            unfiltered.filter { it.version > lastVersion }
        } else if (oldestKnown != null && size != null) {
            check(lastVersion == null)
            version = null
            cache.allItems.dropWhile { it.id != oldestKnown }.drop(1).take(size)
        } else if (size != null) {
            check(oldestKnown == null && lastVersion == null)
            cache.allItems.take(size)
        } else {
            throw UnsupportedOperationException("oldestKnown+lastVersion, oldestKnown+size or size missing")
        }
        return Page(items = items, total = cache.allItems.size, version = version)
    }
}
