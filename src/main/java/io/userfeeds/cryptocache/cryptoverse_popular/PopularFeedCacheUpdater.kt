package io.userfeeds.cryptocache.cryptoverse_popular

import io.userfeeds.cryptocache.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PopularFeedCacheUpdater(
        private val repository: PopularFeedRepository,
        private val api: PopularFeedApi) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        val oldCache = repository.cache
        val idToOldRoot = oldCache.allItems.associateBy { it.id }
        val newAllItems = api.getFeed().blockingFirst().items
        val version = System.currentTimeMillis()
        newAllItems.forEachIndexed { index, current ->
            val oldItem = idToOldRoot[current.id]
            current.version = if (equalByAmountOfRepliesAndLikes(current, oldItem)) oldItem!!.version else version
            if (index != 0) {
                current.after = newAllItems[index - 1].id
            }
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
        val idToOldReply = oldItem.replies.associateBy { it.id }
        newItem.replies.forEach {
            val oldReply = idToOldReply[it.id] ?: return false
            if (it.likes.size != oldReply.likes.size) {
                return false
            }
        }
        return true
    }
}
