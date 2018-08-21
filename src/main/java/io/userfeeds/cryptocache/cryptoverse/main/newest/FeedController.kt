package io.userfeeds.cryptocache.cryptoverse.main.newest

import io.userfeeds.cryptocache.common.Controller
import io.userfeeds.cryptocache.common.Page
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedController(private val repository: FeedRepository) {

    @RequestMapping("/cryptoverse_feed")
    fun getFeed(
            @RequestParam("oldestKnown", required = false) oldestKnown: String?,
            @RequestParam("lastVersion", required = false) lastVersion: Long?,
            @RequestParam("size", required = false) size: Int?): Page {
        return Controller.getFeed(repository, oldestKnown, lastVersion, size)
    }
}
