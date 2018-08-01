package io.userfeeds.cryptocache.opensea

import io.reactivex.rxkotlin.toObservable
import io.userfeeds.cryptocache.FeedItem
import io.userfeeds.cryptocache.context
import io.userfeeds.cryptocache.likes
import io.userfeeds.cryptocache.replies
import org.springframework.stereotype.Component

@Component
class OpenSeaFeedInterceptor(private val openSeaFacade: OpenSeaFacade) {
    fun addOpenSeaData(newAllItems: List<FeedItem>) {
        val openSeaDataByContext = getOpenSeaDataByContext(newAllItems)
        newAllItems.forEach {
            openSeaDataByContext[it.context]?.let { data ->
                it["context_info"] = ContextInfoApiModel(data)
                addReplies(it, openSeaDataByContext)
                addLikes(it, openSeaDataByContext)
            }
        }
    }

    private fun extractContexts(newAllItems: List<FeedItem>): List<String> {
        return newAllItems.flatMap { extractContextsFromItem(it) }.filterNotNull()
    }

    private fun extractContextsFromItem(item: FeedItem): List<String?> {
        return listOf(item.context) + item.replies.flatMap { it.likes.map { it.context } + it.context } + item.likes.map { it.context }
    }

    private fun getOpenSeaDataByContext(newAllItems: List<FeedItem>): Map<String, OpenSeaData> {
        return extractContexts(newAllItems)
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

    private fun addReplies(item: FeedItem, openSeaDataByContext: Map<String, OpenSeaData>) {
        item.replies.forEach { replay ->
            openSeaDataByContext[replay.context]?.let { data ->
                replay["context_info"] = ContextInfoApiModel(data)
                addLikes(replay, openSeaDataByContext)
            }
        }
    }

    private fun addLikes(item: FeedItem, openSeaDataByContext: Map<String, OpenSeaData>) {
        item.likes.forEach { like ->
            openSeaDataByContext[like.context]?.let { data ->
                like["context_info"] = ContextInfoApiModel(data)
            }
        }
    }
}