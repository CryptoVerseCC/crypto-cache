package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.opensea.OpenSeaItemInterceptor

typealias FeedItem = MutableMap<String, Any>

val FeedItem.id
    @Suppress("UNCHECKED_CAST")
    get() = this["id"] as String

var FeedItem.after: String?
    @Suppress("UNCHECKED_CAST")
    get() = throw UnsupportedOperationException()
    set(value) {
        if (value != null) {
            this["after"] = value
        }
    }

val FeedItem.replies
    @Suppress("UNCHECKED_CAST")
    get() = this["replies"] as List<FeedItem>

val FeedItem.likes
    @Suppress("UNCHECKED_CAST")
    get() = this["likes"] as List<FeedItem>

var FeedItem.version
    @Suppress("UNCHECKED_CAST")
    get() = this["version"] as Long
    set(value) {
        this["version"] = value
    }

class FeedItemVisitor : OpenSeaItemInterceptor.Visitor<FeedItem> {
    override fun visit(item: FeedItem, accept: (FeedItem) -> Unit) {
        accept(item)
        item.replies.forEach {
            accept(it)
            it.likes.forEach(accept)
        }
        item.likes.forEach(accept)
    }
}
