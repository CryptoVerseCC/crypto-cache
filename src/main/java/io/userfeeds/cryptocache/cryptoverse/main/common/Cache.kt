package io.userfeeds.cryptocache.cryptoverse.main.common

import io.userfeeds.cryptocache.FeedItem

data class Cache(val allItems: List<FeedItem>, val version: Long)
