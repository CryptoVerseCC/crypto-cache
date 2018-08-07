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
        val contexts = extractContexts(newAllItems, visitor)
        val openSeaDataById = getOpenSeaDataByContext(contexts)
        addContextInfo(newAllItems, visitor, openSeaDataById)
    }

    private fun <T : ContextItem> extractContexts(newAllItems: List<T>, visitor: Visitor<T>): Set<String> {
        val contexts = mutableSetOf<String>()
        newAllItems.forEach { visitor.visit(it) { it.context?.let(contexts::add) } }
        return contexts
    }

    private fun <T : ContextItem> addContextInfo(
            newAllItems: List<T>,
            visitor: Visitor<T>,
            openSeaDataByContext: Map<String, OpenSeaData>) {
        newAllItems.forEach {
            visitor.visit(it) { item ->
                item.context?.let { ctx ->
                    openSeaDataByContext[ctx]?.let { data ->
                        item["context_info"] = ContextInfoApiModel(data)
                    }
                }
            }
        }
    }

    private fun getOpenSeaDataByContext(contexts: Set<String>): Map<String, OpenSeaData> {
        return contexts
                .filter { it.startsWith("ethereum:") }
                .toObservable()
                .buffer(25)
                .concatMap {
                    it.toObservable().flatMap { ctx ->
                        openSeaCache.getData(ctx).map { ctx to it }
                    }
                }
                .toList()
                .blockingGet()
                .toMap()
    }

    interface Visitor<T> {
        fun visit(item: T, accept: (T) -> Unit)
    }
}