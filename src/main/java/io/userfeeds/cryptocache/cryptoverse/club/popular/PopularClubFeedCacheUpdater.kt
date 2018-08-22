package io.userfeeds.cryptocache.cryptoverse.club.popular

import io.userfeeds.contractmapping.CONTRACTS
import io.userfeeds.cryptocache.cryptoverse.club.common.Updater
import io.userfeeds.cryptocache.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PopularClubFeedCacheUpdater(
        private val repository: PopularClubFeedRepository,
        private val api: PopularClubFeedApi) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        CONTRACTS.ALL.forEach {
            val asset = "${it.network}:${it.address}"
            val algo = if (it.is721) "experimental_filter_origin" else "experimental_author_balance"
            Updater.updateCache(repository, api, asset, algo)
        }
        logger.info("Update cache ${javaClass.simpleName}")
    }
}
