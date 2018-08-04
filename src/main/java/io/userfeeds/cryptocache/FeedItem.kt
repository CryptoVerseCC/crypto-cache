package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.opensea.OpenSeaItemInterceptor

typealias FeedItem = MutableMap<String, Any>

val FeedItem.replies
    @Suppress("UNCHECKED_CAST")
    get() = this["replies"] as List<FeedItem>

val FeedItem.likes
    @Suppress("UNCHECKED_CAST")
    get() = this["likes"] as List<FeedItem>

class FeedItemVisitor : OpenSeaItemInterceptor.Visitor<FeedItem> {
    override fun visit(item: FeedItem, f: (FeedItem) -> Unit) {
        f(item)
        item.replies.forEach {
            f(it)
            item.likes.forEach(f)
        }
        item.likes.forEach(f)
    }
}