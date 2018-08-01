package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.opensea.ContextInfoApiModel
import io.userfeeds.cryptocache.opensea.ItemIdExtractor
import io.userfeeds.cryptocache.opensea.OpenSeaData
import io.userfeeds.cryptocache.opensea.OpenSeaDataAddingVisitor

typealias ContextItem = MutableMap<String, Any>

val ContextItem.context
    get() = this["context"] as String?

class ContextItemIdExtractor : ItemIdExtractor<ContextItem> {
    override fun extractContextsFromItem(item: ContextItem): List<String> {
        return listOfNotNull(item.context)
    }
}

class OpenSeaToContextAddingVisitor(private val openSeaDataByContext: Map<String, OpenSeaData>) : OpenSeaDataAddingVisitor<ContextItem> {
    override fun visitItem(item: ContextItem) {
        openSeaDataByContext[item.context]?.let { it ->
            item["context_info"] = ContextInfoApiModel(it)
        }
    }
}