package io.userfeeds.cryptocache.purr

import io.reactivex.Observable
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import retrofit2.http.GET

@AutoRetrofit
interface CryptoPurrApi {

    @GET("cryptopurr_feed;context=ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d;full=true")
    fun getPurrs(): Observable<ItemsWrapper>
}
