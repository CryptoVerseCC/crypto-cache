package io.userfeeds.cryptocache.common

import io.reactivex.Observable
import io.userfeeds.cryptocache.*

object Updater {

    fun updateCache(repository: Repository, api: Any) {
        val oldCache = repository.cache
        val idToOldRoot = oldCache.allItems.associateBy { it.id }
        val apiCall = api.javaClass.getMethod("getFeed").invoke(api) as Observable<ItemsWrapper>
        val newAllItems = apiCall.blockingFirst().items
        val version = System.currentTimeMillis()
        newAllItems.forEachIndexed { index, current ->
            val oldItem = idToOldRoot[current.id]
            current.version = if (equalByAmountOfRepliesAndLikes(current, oldItem)) oldItem!!.version else version
            if (index != 0) {
                current.after = newAllItems[index - 1].id
            }
        }
        repository.cache = Cache(newAllItems, version)
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