package io.userfeeds.cryptocache.cryptoverse_magic

import io.userfeeds.cryptocache.*
import io.userfeeds.cryptocache.common.Updater
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
