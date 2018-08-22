package io.userfeeds.cryptocache.cryptoverse.main.magic

import io.userfeeds.cryptocache.cryptoverse.main.common.Cache
import io.userfeeds.cryptocache.cryptoverse.main.common.Repository
import org.springframework.stereotype.Component

@Component
class MagicFeedRepository : Repository {

    override var cache = Cache(emptyList(), 0)
}
