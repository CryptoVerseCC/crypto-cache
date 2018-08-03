package io.userfeeds.cryptocache.cryptoverse_magic

import io.userfeeds.cryptocache.*
import io.userfeeds.cryptocache.opensea.OpenSeaItemInterceptor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MagicFeedCacheUpdater(private val repository: MagicFeedRepository,
                            private val openSeaItemInterceptor: OpenSeaItemInterceptor) {

    private val api = apiRetrofit().create(MagicFeedApi::class.java)

    //@Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        val oldCache = repository.cache
        val idToOldRoot = oldCache.allItems.associateBy { it["id"] }
        val newAllItems = api.getFeed().blockingFirst().items
        openSeaItemInterceptor.addOpenSeaData(newAllItems, FeedItemVisitor)
        val version = System.currentTimeMillis()
        (listOf(null) + newAllItems).zipWithNext().forEach { (prev, current) ->
            val oldItem = idToOldRoot[current!!["id"]]
            current["version"] = if (equalByAmountOfRepliesAndLikes(current, oldItem)) (oldItem!!["version"] as Long) else version
            prev?.get("id")?.let { current["after"] = it }
        }
        repository.cache = Cache(newAllItems, version)
        logger.info("Update cache ${javaClass.simpleName}")
    }

    private fun equalByAmountOfRepliesAndLikes(newItem: FeedItem, oldItem: FeedItem?): Boolean {
        if (oldItem == null) {
            return false
        }
        if (newItem.likes.size != oldItem.likes.size) {
            return false
        }
        if (newItem.replies.size != oldItem.replies.size) {
            return false
        }
        val idToOldReply = oldItem.replies.associateBy { it["id"] }
        newItem.replies.forEach {
            val oldReply = idToOldReply[it["id"]] ?: return false
            if (it.likes.size != oldReply.likes.size) {
                return false
            }
        }
        return true
    }
}
