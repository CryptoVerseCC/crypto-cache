package io.userfeeds.cryptocache.cryptoverse_discovery

import io.reactivex.Observable
import io.userfeeds.cryptocache.ItemsWrapper
import retrofit2.http.GET
import retrofit2.http.Path

interface DiscoveryApi {

    @GET("experimental_latest_purrers/experimental_author_balance;asset={asset}")
    fun latestPurrers(@Path("asset") asset: String): Observable<ItemsWrapper>

    @GET("experimental_social_profiles;type={type}/experimental_author_balance;asset={asset}")
    fun socialProfiles(@Path("type") type: String, @Path("asset") asset: String): Observable<ItemsWrapper>
}
