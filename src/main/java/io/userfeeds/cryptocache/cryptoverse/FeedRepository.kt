package io.userfeeds.cryptocache.cryptoverse

import org.springframework.stereotype.Component

@Component
class FeedRepository {

    var cache = Cache(emptyList(), 0)
}

data class Cache(val allItems: List<MutableMap<String, Any>>, val version: Long)