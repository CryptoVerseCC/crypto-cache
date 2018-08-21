package io.userfeeds.cryptocache.cryptoverse.club.newest

import io.userfeeds.cryptocache.common.Page
import io.userfeeds.cryptocache.cryptoverse.club.common.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ClubFeedController(private val repository: ClubFeedRepository) {

    @RequestMapping("/cryptoverse_club_feed")
    fun getFeed(
            @RequestParam("id") id: String,
            @RequestParam("oldestKnown", required = false) oldestKnown: String?,
            @RequestParam("lastVersion", required = false) lastVersion: Long?,
            @RequestParam("size", required = false) size: Int?): Page {
        return Controller.getFeed(repository, id, oldestKnown, lastVersion, size)
    }
}
