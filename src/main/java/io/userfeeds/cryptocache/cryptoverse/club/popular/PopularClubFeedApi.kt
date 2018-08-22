package io.userfeeds.cryptocache.cryptoverse.club.popular

import io.reactivex.Observable
import io.userfeeds.cryptocache.FeedItemVisitor
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.opensea.OpenSeaDecorator
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import retrofit2.http.GET
import retrofit2.http.Path

@AutoRetrofit
@OpenSeaDecorator(FeedItemVisitor::class)
interface PopularClubFeedApi {

    @GET("cryptoverse_club_last_week_popular_feed;id={id}/{algo};asset={id}")
    fun getFeed(@Path("id") id: String, @Path("algo") algo: String): Observable<ItemsWrapper>
}
