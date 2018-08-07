package io.userfeeds.cryptocache.cryptoverse

import io.userfeeds.cryptocache.FeedItem
import io.userfeeds.cryptocache.likes
import io.userfeeds.cryptocache.logger
import io.userfeeds.cryptocache.replies
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FeedCacheUpdater(
        private val repository: FeedRepository,
        private val api: FeedApi) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        val oldCache = repository.cache
        val idToOldRoot = oldCache.allItems.associateBy { it["id"] }
        val newAllItems = api.getFeed().blockingFirst().items
        val version = System.currentTimeMillis()
        newAllItems.forEach {
            val oldItem = idToOldRoot[it["id"]]
            it["version"] = if (equalByAmountOfRepliesAndLikes(it, oldItem)) (oldItem!!["version"] as Long) else version
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
        val idToOldReply = (oldItem.replies).associateBy { it["id"] }
        (newItem.replies).forEach {
            val oldReply: FeedItem = idToOldReply[it["id"]] ?: return false
            if (it.likes.size != oldReply.likes.size) {
                return false
            }
        }
        return true
    }
}