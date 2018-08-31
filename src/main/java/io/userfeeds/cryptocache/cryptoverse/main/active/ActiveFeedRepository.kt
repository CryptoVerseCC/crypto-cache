package io.userfeeds.cryptocache.cryptoverse.main.active

import io.userfeeds.cryptocache.cryptoverse.main.common.Cache
import io.userfeeds.cryptocache.cryptoverse.main.common.Repository
import org.springframework.stereotype.Component

@Component
class ActiveFeedRepository : Repository {

    override var cache = Cache(emptyList(), 0)
}
