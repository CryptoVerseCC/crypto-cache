package io.userfeeds.cryptocache.cryptoverse

import io.reactivex.Observable
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import io.userfeeds.cryptocache.opensea.OpenSeaDecorator
import retrofit2.http.GET

@AutoRetrofit
@OpenSeaDecorator
interface FeedApi {

    @GET("cryptoverse_feed")
    fun getFeed(): Observable<ItemsWrapper>
}
