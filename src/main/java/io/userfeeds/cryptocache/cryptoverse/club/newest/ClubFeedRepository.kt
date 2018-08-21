package io.userfeeds.cryptocache.cryptoverse.club.newest

import io.userfeeds.cryptocache.common.Cache
import io.userfeeds.cryptocache.cryptoverse.club.common.Repository
import org.springframework.stereotype.Component

@Component
class ClubFeedRepository : Repository {

    override var cache = mutableMapOf<String, Cache>().withDefault { Cache(emptyList(), 0) }
}
