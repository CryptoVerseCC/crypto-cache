package io.userfeeds.cryptocache.cryptoverse_discovery

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DiscoveryController(private val repository: DiscoveryRepository, private val updater: DiscoveryCacheUpdater) {

    @RequestMapping("/cryptoverse_discovery")
    fun getDiscovery(@RequestParam("asset") asset: String): Discovery {
        var discovery = repository.get(asset)!!
//        if (discovery == null) {
//            updater.updateForAsset(asset)
//            discovery = repository.get(asset)!!
//        }
        return discovery
    }
}
