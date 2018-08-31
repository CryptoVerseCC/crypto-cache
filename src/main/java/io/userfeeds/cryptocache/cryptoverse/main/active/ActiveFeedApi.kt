package io.userfeeds.cryptocache.cryptoverse.main.active

import io.reactivex.Observable
import io.userfeeds.cryptocache.FeedItemVisitor
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.opensea.OpenSeaDecorator
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import retrofit2.http.GET

@AutoRetrofit
@OpenSeaDecorator(FeedItemVisitor::class)
interface ActiveFeedApi {

    @GET("cryptoverse_feed_active")
    fun getFeed(): Observable<ItemsWrapper>
}
