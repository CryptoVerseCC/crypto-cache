package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import io.userfeeds.cryptocache.ContextItem
import io.userfeeds.cryptocache.FeedItemDataAdder
import org.springframework.stereotype.Component

@Component
class OpenSeaItemInterceptor(private val openSeaFacade: OpenSeaFacade) {
    fun <T : ContextItem> addOpenSeaData(newAllItems: List<T>, dataAdderCreator: (Map<String, OpenSeaData>) -> OpenSeaDataAdder<T>, itemContextExtractor: ItemContextExtractor<T>) {
        val openSeaDataByContext: Map<String, OpenSeaData> = getOpenSeaDataByContext(newAllItems, itemContextExtractor)
        val adder = dataAdderCreator(openSeaDataByContext)
        newAllItems.forEach {
            adder.addOpenSeaDataToItem(it)
        }
    }

    private fun <T : ContextItem> extractContexts(newAllItems: List<T>, itemContextExtractor: ItemContextExtractor<T>): List<String> {
        return newAllItems.flatMap { itemContextExtractor.extractContextsFromItem(it) }.filterNotNull()
    }

    private fun <T : ContextItem> getOpenSeaDataByContext(newAllItems: List<T>,
                                                          itemContextExtractor: ItemContextExtractor<T>): Map<String, OpenSeaData> {
        return extractContexts(newAllItems, itemContextExtractor)
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

interface OpenSeaDataAdder<T> {
    fun addOpenSeaDataToItem(item: T)
}

interface ItemContextExtractor<T> {
    fun extractContextsFromItem(item: T): List<String?>
}