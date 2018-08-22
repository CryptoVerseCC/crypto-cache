package io.userfeeds.cryptocache.cryptoverse.club.popular

import io.userfeeds.cryptocache.cryptoverse.main.common.Cache
import io.userfeeds.cryptocache.cryptoverse.club.common.Repository
import org.springframework.stereotype.Component

@Component
class PopularClubFeedRepository : Repository {

    override var cache = mutableMapOf<String, Cache>().withDefault { Cache(emptyList(), 0) }
}
