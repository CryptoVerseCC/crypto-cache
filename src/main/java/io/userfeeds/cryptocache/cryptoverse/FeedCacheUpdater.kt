package io.userfeeds.cryptocache.cryptoverse

import io.userfeeds.cryptocache.*
import io.userfeeds.cryptocache.opensea.OpenSeaItemInterceptor
import okhttp3.OkHttpClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Component
class FeedCacheUpdater(private val repository: FeedRepository,
                       private val openSeaItemInterceptor: OpenSeaItemInterceptor) {

    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(apiBaseUrl)
            .client(OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build())
            .build()
            .create(FeedApi::class.java)

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
        openSeaItemInterceptor.addOpenSeaData(newAllItems, ::OpenSeaToFeedAddingVisitor, FeedItemIdExtractor())
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