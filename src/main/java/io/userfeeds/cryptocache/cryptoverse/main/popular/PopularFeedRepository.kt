package io.userfeeds.cryptocache.cryptoverse.main.popular

import io.userfeeds.cryptocache.FeedItem
import io.userfeeds.cryptocache.common.Cache
import io.userfeeds.cryptocache.common.Repository
import org.springframework.stereotype.Component

@Component
class PopularFeedRepository : Repository {

    override var cache = Cache(emptyList(), 0)
}
