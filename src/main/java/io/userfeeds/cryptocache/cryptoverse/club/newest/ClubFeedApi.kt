package io.userfeeds.cryptocache.cryptoverse.club.newest

import io.reactivex.Observable
import io.userfeeds.cryptocache.FeedItemVisitor
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.opensea.OpenSeaDecorator
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import retrofit2.http.GET
import retrofit2.http.Path

@AutoRetrofit
@OpenSeaDecorator(FeedItemVisitor::class)
interface ClubFeedApi {

    @GET("cryptoverse_club_feed;id={id}/{algo};asset={id}")
    fun getFeed(@Path("id") id: String, @Path("algo") algo: String): Observable<ItemsWrapper>
}
