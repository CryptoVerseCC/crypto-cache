package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import io.userfeeds.cryptocache.ContextItem
import org.springframework.stereotype.Component

@Component
class OpenSeaItemInterceptor(private val openSeaFacade: OpenSeaFacade) {

    fun <T : ContextItem> addOpenSeaData(newAllItems: List<T>, dataAddingVisitorCreator: (Map<String, OpenSeaData>) -> OpenSeaDataAddingVisitor<T>, itemIdExtractor: ItemIdExtractor<T>) {
        val openSeaDataByContext: Map<String, OpenSeaData> = getOpenSeaDataByContext(newAllItems, itemIdExtractor)
        val adder = dataAddingVisitorCreator(openSeaDataByContext)
        newAllItems.forEach {
            adder.visitItem(it)
        }
    }

    private fun <T : ContextItem> extractContexts(newAllItems: List<T>, itemIdExtractor: ItemIdExtractor<T>): List<String> {
        return newAllItems.flatMap { itemIdExtractor.extractContextsFromItem(it) }
    }

    private fun <T : ContextItem> getOpenSeaDataByContext(newAllItems: List<T>,
                                                          itemIdExtractor: ItemIdExtractor<T>): Map<String, OpenSeaData> {
        return extractContexts(newAllItems, itemIdExtractor)
                .distinct()
                .filter { it.startsWith("ethereum:") }
                .toObservable()
                .buffer(25)
                .concatMap {
                    it.toObservable().flatMap { ctx ->
                        openSeaFacade.asset(ctx).map { it to ctx }
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