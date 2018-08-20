package io.userfeeds.cryptocache.cryptoverse

import io.userfeeds.cryptocache.common.Updater
import io.userfeeds.cryptocache.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FeedCacheUpdater(
        private val repository: FeedRepository,
        private val api: FeedApi) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        Updater.updateCache(repository, api)
        logger.info("Update cache ${javaClass.simpleName}")
    }
}
