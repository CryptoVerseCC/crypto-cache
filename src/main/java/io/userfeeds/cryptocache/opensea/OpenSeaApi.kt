package io.userfeeds.cryptocache.opensea

import com.squareup.moshi.Json
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenSeaApi {

    @GET("asset/{address}/{token}/")
    fun loadData(
            @Path("address") address: String,
            @Path("token") token: String
    ): Observable<OpenSeaDataFromApi>

    @GET("assets/?limit=100")
    fun loadDataMultiple(
            @Query("asset_contract_address") address: String,
            @Query("token_ids") tokens: List<String>
    ): Observable<AssetsWrapperFromApi>

    @GET("assets/?order_by=current_price&order_direction=asc&limit=10")
    fun cheapTokens(
            @Query("asset_contract_address") contractAddress: String
    ): Observable<AssetsWrapperFromApi>

    data class AssetsWrapperFromApi(
            @Json(name = "assets") val assets: List<OpenSeaDataFromApi>
    )

    data class OpenSeaDataFromApi(
            @Json(name = "token_id") val tokenId: String,
            @Json(name = "background_color") val backgroundColor: String?,
            @Json(name = "external_link") val externalLink: String?,
            @Json(name = "name") val name: String?,
            @Json(name = "image_url") val imageUrl: String?,
            @Json(name = "image_preview_url") val imagePreviewUrl: String?,
            @Json(name = "owner") val owner: OpenSeaOwner,
            @Json(name = "current_price") val currentPrice: String?,
            @Json(name = "sell_orders") val sellOrders: List<Map<String, Any>>?
    )

    data class OpenSeaOwner(
            @Json(name = "address") val address: String
    )
}
