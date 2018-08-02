package io.userfeeds.cryptocache.purr

import io.userfeeds.cryptocache.apiRetrofit
import io.userfeeds.cryptocache.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CacheUpdater(private val store: Store) {

    private val api = apiRetrofit().create(CryptoPurrApi::class.java)

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        store.cache = api.getPurrs().blockingFirst().items
        logger.info("Update cache ${javaClass.simpleName}")
    }
}
