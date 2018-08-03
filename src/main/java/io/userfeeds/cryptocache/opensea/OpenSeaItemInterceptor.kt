package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import io.userfeeds.cryptocache.ContextItem
import io.userfeeds.cryptocache.context
import org.springframework.stereotype.Component

@Component
class OpenSeaItemInterceptor(private val openSeaCache: OpenSeaCache) {

    fun <T : ContextItem> addOpenSeaData(
            newAllItems: List<T>,
            visitor: Visitor<T>) {
        val ids = extractIds(newAllItems, visitor)
        val openSeaDataById = getOpenSeaDataByContext(ids)
        addContextInfo(newAllItems, visitor, openSeaDataById)
    }

    private fun <T : ContextItem> extractIds(newAllItems: List<T>, visitor: Visitor<T>): List<String> {
        val ids = mutableListOf<String>()
        newAllItems.forEach { visitor.visit(it) { it.context?.let { ids.add(it) } } }
        return ids
    }

    private fun <T : ContextItem> addContextInfo(
            newAllItems: List<T>,
            visitor: Visitor<T>,
            openSeaDataByContext: Map<String, OpenSeaData>): List<String> {
        val ids = mutableListOf<String>()
        newAllItems.forEach {
            visitor.visit(it) { item ->
                item.context?.let { ctx ->
                    openSeaDataByContext[ctx]?.let { data ->
                        item["context_info"] = ContextInfoApiModel(data)
                    }
                }
            }
        }
        return ids
    }

    private fun getOpenSeaDataByContext(ids: List<String>): Map<String, OpenSeaData> {
        return ids
                .distinct()
                .filter { it.startsWith("ethereum:") }
                .toObservable()
                .buffer(25)
                .concatMap {
                    it.toObservable().flatMap { ctx ->
                        openSeaCache.asset(ctx).map { it to ctx }
                    }
                }
                .toList()
                .blockingGet()
                .map { it.second to it.first }
                .toMap()
    }

    interface Visitor<T> {
        fun visit(item: T, f: (T) -> Unit)
    }
}