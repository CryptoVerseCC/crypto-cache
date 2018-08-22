package io.userfeeds.cryptocache.cryptoverse.main.magic

import io.userfeeds.cryptocache.*
import io.userfeeds.cryptocache.cryptoverse.main.common.Updater
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MagicFeedCacheUpdater(
        private val repository: MagicFeedRepository,
        private val api: MagicFeedApi) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        Updater.updateCache(repository, api)
        logger.info("Update cache ${javaClass.simpleName}")
    }
}
