package io.userfeeds.cryptocache.cryptoverse.main.coiners

import io.userfeeds.cryptocache.cryptoverse.main.common.Updater
import io.userfeeds.cryptocache.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CoinersFeedCacheUpdater(
        private val repository: CoinersFeedRepository,
        private val api: CoinersFeedApi) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        Updater.updateCache(repository, api)
        logger.info("Update cache ${javaClass.simpleName}")
    }
}
