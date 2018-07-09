package io.userfeeds.cryptocache.cryptoverse

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Component
class FeedCacheUpdater(private val repository: FeedRepository) {

    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl("https://api.userfeeds.io/ranking/")
            .client(OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor())
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build())
            .build()
            .create(FeedApi::class.java)

    @Scheduled(fixedDelay = 5_000)
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
    }

    private fun equalByAmountOfRepliesAndLikes(newItem: MutableMap<String, Any>, oldItem: MutableMap<String, Any>?): Boolean {
        if (oldItem == null) {
            return false
        }
        if ((newItem["likes"] as List<*>).size != (oldItem["likes"] as List<*>).size) {
            return false
        }
        if ((newItem["replies"] as List<*>).size != (oldItem["replies"] as List<*>).size) {
            return false
        }
        val idToOldReply = (oldItem["replies"] as List<Map<String, Any>>).associateBy { it["id"] }
        (newItem["replies"] as List<Map<String, Any>>).forEach {
            val oldReply = idToOldReply[it["id"]] ?: return false
            if ((it["likes"] as List<*>).size != (oldReply["likes"] as List<*>).size) {
                return false
            }
        }
        return true
    }
}
