package io.userfeeds

import io.reactivex.Observable
import retrofit2.http.GET

interface CryptoPurrApi {

    @GET("cryptopurr_feed;context=ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d")
    fun getPurrs() : Observable<ItemsWrapper>
}

data class ItemsWrapper(val items:List<Map<String,Any>>)