package io.userfeeds.cryptocache.cryptoverse_magic

import org.springframework.stereotype.Component

@Component
class MagicFeedRepository {

    var cache = Cache(emptyList(), 0)
}

data class Cache(val allItems: List<MutableMap<String, Any>>, val version: Long)
