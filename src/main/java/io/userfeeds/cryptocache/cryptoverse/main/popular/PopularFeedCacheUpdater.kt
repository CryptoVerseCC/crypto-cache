package io.userfeeds.cryptocache.cryptoverse.main.popular

import io.userfeeds.cryptocache.common.Updater
import io.userfeeds.cryptocache.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PopularFeedCacheUpdater(
        private val repository: PopularFeedRepository,
        private val api: PopularFeedApi) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        Updater.updateCache(repository, api)
        logger.info("Update cache ${javaClass.simpleName}")
    }
}
