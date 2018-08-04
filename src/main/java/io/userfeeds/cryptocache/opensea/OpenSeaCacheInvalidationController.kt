package io.userfeeds.cryptocache.opensea

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OpenSeaCacheInvalidationController(private val openSeaCacheUpdater: OpenSeaCacheUpdater) {

    @RequestMapping("/invalidate_opensea_cache")
    fun invalidateCache() {
        openSeaCacheUpdater.updateCache()
    }
}