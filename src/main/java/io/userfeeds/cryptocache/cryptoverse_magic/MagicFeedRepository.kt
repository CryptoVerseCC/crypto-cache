package io.userfeeds.cryptocache.cryptoverse_magic

import io.userfeeds.cryptocache.FeedItem
import org.springframework.stereotype.Component

@Component
class MagicFeedRepository {

    var cache = Cache(emptyList(), 0)
}

data class Cache(val allItems: List<FeedItem>, val version: Long)
