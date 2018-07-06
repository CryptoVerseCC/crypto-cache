package io.userfeeds.cryptocache.cryptoverse

import io.reactivex.Observable
import retrofit2.http.GET

interface CryptoverseFeedApi {

    @GET("cryptoverse_feed")
    fun getFeed(): Observable<ItemsWrapper>
}

data class ItemsWrapper(val items: List<MutableMap<String, Any>>)
