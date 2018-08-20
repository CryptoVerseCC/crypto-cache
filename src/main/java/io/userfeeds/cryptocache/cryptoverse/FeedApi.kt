package io.userfeeds.cryptocache.cryptoverse

import io.reactivex.Observable
import io.userfeeds.cryptocache.FeedItemVisitor
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.common.Api
import io.userfeeds.cryptocache.opensea.OpenSeaDecorator
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import retrofit2.http.GET

@AutoRetrofit
@OpenSeaDecorator(FeedItemVisitor::class)
interface FeedApi : Api {

    @GET("cryptoverse_feed")
    override fun getFeed(): Observable<ItemsWrapper>
}
