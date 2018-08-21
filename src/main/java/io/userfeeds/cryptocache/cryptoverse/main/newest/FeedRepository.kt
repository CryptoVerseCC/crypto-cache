package io.userfeeds.cryptocache.cryptoverse.main.newest

import io.userfeeds.cryptocache.common.Cache
import io.userfeeds.cryptocache.common.Repository
import org.springframework.stereotype.Component

@Component
class FeedRepository : Repository {

    override var cache = Cache(emptyList(), 0)
}
