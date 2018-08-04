package io.userfeeds.cryptocache.cryptoverse_discovery

import io.reactivex.Observable
import io.userfeeds.cryptocache.ContextItemVisitor
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.opensea.OpenSeaDecorator
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import retrofit2.http.GET
import retrofit2.http.Path

@AutoRetrofit
@OpenSeaDecorator(ContextItemVisitor::class)
interface DiscoveryApi {

    @GET("experimental_latest_purrers/{name};asset={asset}")
    fun latestPurrers(@Path("name") name: String, @Path("asset") asset: String): Observable<ItemsWrapper>

    @GET("experimental_social_profiles;type={type}/{name};asset={asset}")
    fun socialProfiles(@Path("type") type: String, @Path("name") name: String, @Path("asset") asset: String): Observable<ItemsWrapper>
}
