package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import io.userfeeds.cryptocache.ContextItem
import io.userfeeds.cryptocache.about
import io.userfeeds.cryptocache.context
import io.userfeeds.cryptocache.target
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
        newAllItems.forEach { rootItem ->
            visitor.visit(rootItem) { item ->
                item.target?.takeIf { it.matches(Regex("^ethereum:0x[0-9a-f]{40}:\\d+$")) }?.let(contexts::add)
                item.context?.let(contexts::add)
                item.about?.takeIf { it.matches(Regex("^ethereum:0x[0-9a-f]{40}:\\d+$")) }?.let(contexts::add)
            }
        }
        return contexts
    }

    private fun <T : ContextItem> addContextInfo(
            newAllItems: List<T>,
            visitor: Visitor<T>,
            openSeaDataByContext: Map<String, OpenSeaData>) {
        newAllItems.forEach { rootItem ->
            visitor.visit(rootItem) { item ->
                item.target?.let { ctx ->
                    val targetData = openSeaDataByContext[ctx]
                    if (targetData != null) {
                        item["target_info"] = ContextInfoApiModel(targetData)
                    }
                }
                item.context?.let { ctx ->
                    val contextData = openSeaDataByContext[ctx]
                    if (contextData != null) {
                        item["context_info"] = ContextInfoApiModel(contextData)
                    }
                }
                item.about?.let { about ->
                    val aboutData = openSeaDataByContext[about]
                    if (aboutData != null) {
                        item["about_info"] = ContextInfoApiModel(aboutData)
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
