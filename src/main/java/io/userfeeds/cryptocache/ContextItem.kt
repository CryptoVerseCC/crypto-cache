package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.opensea.ContextInfoApiModel
import io.userfeeds.cryptocache.opensea.ItemContextExtractor
import io.userfeeds.cryptocache.opensea.OpenSeaData
import io.userfeeds.cryptocache.opensea.OpenSeaDataAdder

typealias ContextItem = MutableMap<String, Any>

val ContextItem.context
    get() = this["context"] as String?

class ContextItemContextExtractor : ItemContextExtractor<ContextItem> {
    override fun extractContextsFromItem(item: ContextItem): List<String> {
        return listOfNotNull(item.context)
    }
}

class ContextdItemDataAdder(private val openSeaDataByContext: Map<String, OpenSeaData>) : OpenSeaDataAdder<ContextItem> {
    override fun addOpenSeaDataToItem(item: ContextItem) {
        openSeaDataByContext[item.context]?.let { it ->
            item["context_info"] = ContextInfoApiModel(it)
        }
    }
}