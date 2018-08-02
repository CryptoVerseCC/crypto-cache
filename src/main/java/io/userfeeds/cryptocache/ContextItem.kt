package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.opensea.OpenSeaItemInterceptor


typealias ContextItem = MutableMap<String, Any>

val ContextItem.context
    get() = this["context"] as String?

object ContextItemVisitor : OpenSeaItemInterceptor.Visitor<ContextItem> {
    override fun visit(item: ContextItem, f: (ContextItem) -> Unit) = f(item)
}