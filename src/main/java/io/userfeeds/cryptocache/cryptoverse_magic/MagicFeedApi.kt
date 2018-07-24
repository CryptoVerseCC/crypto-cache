package io.userfeeds.cryptocache.cryptoverse_magic

import io.reactivex.Observable
import retrofit2.http.GET

interface MagicFeedApi {

    @GET("cryptoverse_feed/experimental_root_score_1;asset=ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d;asset=ethereum:0xf7a6e15dfd5cdd9ef12711bd757a9b6021abf643;asset=ethereum:0xa6d954d08877f8ce1224f6bfb83484c7d3abf8e9;asset=ethereum:0x323a3e1693e7a0959f65972f3bf2dfcb93239dfe;asset=ethereum:0xfa6f7881e52fdf912c4a285d78a3141b089ce859")
    fun getFeed(): Observable<ItemsWrapper>
}

data class ItemsWrapper(val items: List<MutableMap<String, Any>>)
