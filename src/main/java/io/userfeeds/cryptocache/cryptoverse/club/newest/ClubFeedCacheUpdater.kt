package io.userfeeds.cryptocache.cryptoverse.club.newest

import io.userfeeds.cryptocache.common.ContractsProvider
import io.userfeeds.cryptocache.cryptoverse.club.common.Updater
import io.userfeeds.cryptocache.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ClubFeedCacheUpdater(
        private val repository: ClubFeedRepository,
        private val api: ClubFeedApi,
        private val contractsProvider: ContractsProvider) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        contractsProvider.get().forEach {
            val asset = "${it.network}:${it.address}"
            val algo = if (it.is721) "experimental_filter_origin" else "experimental_author_balance"
            Updater.updateCache(repository, api, asset, algo)
        }
        logger.info("Update cache ${javaClass.simpleName}")
    }
}
