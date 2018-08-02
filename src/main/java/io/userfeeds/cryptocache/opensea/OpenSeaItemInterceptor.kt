package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import io.userfeeds.cryptocache.ContextItem
import org.springframework.stereotype.Component

@Component
class OpenSeaItemInterceptor(private val openSeaCache: OpenSeaCache) {

    fun <T : ContextItem> addOpenSeaData(newAllItems: List<T>,
                                         dataAddingVisitorCreator: (Map<String, OpenSeaData>) -> OpenSeaDataAddingVisitor<T>,
                                         itemIdExtractor: ItemIdExtractor<T>) {
        val ids = extractIds(newAllItems, itemIdExtractor)
        val openSeaDataById = getOpenSeaDataByContext(ids)
        val visitor = dataAddingVisitorCreator(openSeaDataById)
        newAllItems.forEach {
            visitor.visitItem(it)
        }
    }

    private fun <T : ContextItem> extractIds(newAllItems: List<T>, itemIdExtractor: ItemIdExtractor<T>): List<String> {
        return newAllItems.flatMap { itemIdExtractor.extractContextsFromItem(it) }
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
}

interface OpenSeaDataAddingVisitor<T> {
    fun visitItem(item: T)
}

interface ItemIdExtractor<T> {
    fun extractContextsFromItem(item: T): List<String>
}