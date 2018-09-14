package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.cryptoverse.main.active.ActiveFeedRepository
import io.userfeeds.cryptocache.cryptoverse.main.coiners.CoinersFeedRepository
import io.userfeeds.cryptocache.cryptoverse.main.magic.MagicFeedRepository
import io.userfeeds.cryptocache.cryptoverse.main.newest.FeedRepository
import io.userfeeds.cryptocache.cryptoverse.main.popular.PopularFeedRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckRestController(
        private val feedRepository: FeedRepository,
        private val popularFeedRepository: PopularFeedRepository,
        private val magicFeedRepository: MagicFeedRepository,
        private val activeFeedRepository: ActiveFeedRepository,
        private val coinersFeedRepository: CoinersFeedRepository) {

    @GetMapping("/health_check")
    fun isReady() {
        check(feedRepository.cache.allItems.isNotEmpty()
                && popularFeedRepository.cache.allItems.isNotEmpty()
                && magicFeedRepository.cache.allItems.isNotEmpty()
                && activeFeedRepository.cache.allItems.isNotEmpty()
                && coinersFeedRepository.cache.allItems.isNotEmpty()
        ) { "API is not ready." }
    }
}
