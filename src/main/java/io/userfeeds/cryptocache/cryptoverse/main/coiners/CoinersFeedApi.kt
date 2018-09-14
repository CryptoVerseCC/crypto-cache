package io.userfeeds.cryptocache.cryptoverse.main.coiners

import io.reactivex.Observable
import io.userfeeds.cryptocache.FeedItemVisitor
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.opensea.OpenSeaDecorator
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import retrofit2.http.GET

@AutoRetrofit
@OpenSeaDecorator(FeedItemVisitor::class)
interface CoinersFeedApi {

    @GET("cryptoverse_coiners_simple_feed")
    fun getFeed(): Observable<ItemsWrapper>
}
