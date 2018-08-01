package io.userfeeds.cryptocache.cryptoverse

import io.reactivex.rxkotlin.toObservable
import io.userfeeds.cryptocache.apiBaseUrl
import io.userfeeds.cryptocache.logger
import io.userfeeds.cryptocache.opensea.OpenSeaData
import io.userfeeds.cryptocache.opensea.OpenSeaFacade
import okhttp3.OkHttpClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Component
class FeedCacheUpdater(private val repository: FeedRepository,
                       private val openSeaFacade: OpenSeaFacade) {

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
        val openSeaDataByContext = getOpenSeaDataByContext(newAllItems)
        addOpenSeaData(newAllItems, openSeaDataByContext)
        repository.cache = Cache(newAllItems, version)
        logger.info("Update cache ${javaClass.simpleName}")
    }

    private fun getOpenSeaDataByContext(newAllItems: List<DynamicItem>): Map<String, OpenSeaData> {
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

    private fun extractContexts(newAllItems: List<DynamicItem>): List<String> {
        return newAllItems.flatMap { extractContextsFromItem(it) }.filterNotNull()
    }

    private fun extractContextsFromItem(item: DynamicItem): List<String?> {
        return listOf(item.context) + item.replies.flatMap { it.likes.map { it.context } + it.context } + item.likes.map { it.context }
    }

    private fun addOpenSeaData(newAllItems: List<DynamicItem>, openSeaDataByContext: Map<String, OpenSeaData>) {
        newAllItems.forEach {
            openSeaDataByContext[it.context]?.let { data ->
                it["context_info"] = ContextInfoApiModel(data)
                addReplies(it, openSeaDataByContext)
                addLikes(it, openSeaDataByContext)
            }
        }
    }

    private fun addReplies(item: DynamicItem, openSeaDataByContext: Map<String, OpenSeaData>) {
        item.replies.forEach { replay ->
            openSeaDataByContext[replay.context]?.let { data ->
                replay["context_info"] = ContextInfoApiModel(data)
                addLikes(replay, openSeaDataByContext)
            }
        }
    }

    private fun addLikes(item: DynamicItem, openSeaDataByContext: Map<String, OpenSeaData>) {
        item.likes.forEach { like ->
            openSeaDataByContext[like.context]?.let { data ->
                like["context_info"] = ContextInfoApiModel(data)
            }
        }
    }

    private fun equalByAmountOfRepliesAndLikes(newItem: DynamicItem, oldItem: DynamicItem?): Boolean {
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
            val oldReply: DynamicItem = idToOldReply[it["id"]] ?: return false
            if (it.likes.size != oldReply.likes.size) {
                return false
            }
        }
        return true
    }

    private val DynamicItem.replies
        @Suppress("UNCHECKED_CAST")
        get() = this["replies"] as List<DynamicItem>

    private val DynamicItem.likes
        @Suppress("UNCHECKED_CAST")
        get() = this["likes"] as List<DynamicItem>

    private val DynamicItem.context
        get() = this["context"] as String?
}

typealias DynamicItem = MutableMap<String, Any>


private data class ContextInfoApiModel(
        val background_color: String?,
        val owner: String,
        val image_url: String,
        val name: String
) {
    constructor(openSeaData: OpenSeaData) :
            this(openSeaData.backgroundColor, openSeaData.owner, openSeaData.imageUrl, openSeaData.name)
}