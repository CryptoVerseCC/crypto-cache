package io.userfeeds.cryptocache.common

import io.userfeeds.cryptocache.FeedItem

data class Cache(val allItems: List<FeedItem>, val version: Long)
