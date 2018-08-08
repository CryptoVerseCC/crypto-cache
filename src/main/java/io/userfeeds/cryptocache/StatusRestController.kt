package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.cryptoverse.FeedRepository
import io.userfeeds.cryptocache.cryptoverse_magic.MagicFeedRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusRestController(private val feedRepository: FeedRepository,
                           private val magicFeedRepository: MagicFeedRepository) {

    @GetMapping("/status")
    fun getStatus() {
        check(feedRepository.cache.allItems.isNotEmpty() && magicFeedRepository.cache.allItems.isNotEmpty()) { "API is not ready." }
    }
}