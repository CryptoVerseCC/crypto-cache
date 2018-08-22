package io.userfeeds.cryptocache.cryptoverse.discovery

import io.userfeeds.cryptocache.common.Contract
import io.userfeeds.cryptocache.common.ContractsProvider
import io.userfeeds.cryptocache.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DiscoveryCacheUpdater(
        private val repository: DiscoveryRepository,
        private val api: DiscoveryApi,
        private val contractsProvider: ContractsProvider) {

    @Scheduled(fixedDelay = 1_000)
    fun updateCache() {
        val contracts = contractsProvider.get()
        contracts.forEach { updateForAsset(it.asset, it.type) }
        logger.info("Update cache ${contracts.size} ${javaClass.simpleName}")
    }

    fun updateForAsset(asset: String, type: Contract.Type) {
        try {
            val name = if (type == Contract.Type.erc20) "experimental_author_balance" else "experimental_filter_origin"
            val latest = api.latestPurrers(name, asset).blockingFirst().items
            val twitter = api.socialProfiles("twitter", name, asset).blockingFirst().items
            val facebook = api.socialProfiles("facebook", name, asset).blockingFirst().items
            val instagram = api.socialProfiles("instagram", name, asset).blockingFirst().items
            val github = api.socialProfiles("github", name, asset).blockingFirst().items
            repository.put(asset, Discovery(latest, twitter, facebook, instagram, github))
            logger.debug("Update cache: $asset ${javaClass.simpleName}")
        } catch (exception: Throwable) {
            logger.warn("Exception during cache update: $asset ${javaClass.simpleName}", exception)
        }
    }
}
