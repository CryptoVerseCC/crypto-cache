package io.userfeeds.cryptocache.cryptoverse.discovery

import io.userfeeds.cryptocache.cryptoverse.main.common.Contract
import io.userfeeds.cryptocache.cryptoverse.main.common.ContractsProvider
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
        contracts.forEach(this::updateForAsset)
        logger.info("Update cache ${contracts.size} ${javaClass.simpleName}")
    }

    private fun updateForAsset(contract: Contract) {
        val asset = contract.asset
        try {
            val discovery = loadDiscovery(asset, contract.is721)
            repository.put(asset, discovery)
            logger.debug("Update cache: $asset ${javaClass.simpleName}")
        } catch (exception: Throwable) {
            logger.warn("Exception during cache update: $asset ${javaClass.simpleName}", exception)
        }
    }

    fun loadDiscovery(asset: String, is721: Boolean = false): Discovery {
        val name = if (is721) "experimental_filter_origin" else "experimental_author_balance"
        val latest = api.latestPurrers(name, asset).blockingFirst().items
        val twitter = api.socialProfiles("twitter", name, asset).blockingFirst().items
        val facebook = api.socialProfiles("facebook", name, asset).blockingFirst().items
        val instagram = api.socialProfiles("instagram", name, asset).blockingFirst().items
        val github = api.socialProfiles("github", name, asset).blockingFirst().items
        return Discovery(latest, twitter, facebook, instagram, github)
    }
}
