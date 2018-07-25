package io.userfeeds.cryptocache.cryptoverse

import io.reactivex.Observable
import io.userfeeds.cryptocache.ItemsWrapper
import retrofit2.http.GET

interface FeedApi {

    @GET("cryptoverse_feed")
    fun getFeed(): Observable<ItemsWrapper>
}
