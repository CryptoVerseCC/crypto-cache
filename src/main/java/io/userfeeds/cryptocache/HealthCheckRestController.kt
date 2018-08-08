package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.cryptoverse.FeedRepository
import io.userfeeds.cryptocache.cryptoverse_magic.MagicFeedRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckRestController(private val feedRepository: FeedRepository,
                                private val magicFeedRepository: MagicFeedRepository) {

    @GetMapping("/health_check")
    fun isAlive() {
        check(feedRepository.cache.allItems.isNotEmpty() && magicFeedRepository.cache.allItems.isNotEmpty()) { "API is not ready." }
    }
}