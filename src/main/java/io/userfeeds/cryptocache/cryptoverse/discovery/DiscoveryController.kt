package io.userfeeds.cryptocache.cryptoverse.discovery

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DiscoveryController(private val repository: DiscoveryRepository, private val updater: DiscoveryCacheUpdater) {

    @RequestMapping("/cryptoverse_discovery")
    fun getDiscovery(@RequestParam("asset") asset: String): Discovery {
        return repository.get(asset) ?: updater.loadDiscovery(asset)
    }
}
