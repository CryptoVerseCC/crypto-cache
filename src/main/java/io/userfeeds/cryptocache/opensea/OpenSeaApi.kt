package io.userfeeds.cryptocache.opensea

import com.squareup.moshi.Json
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenSeaApi {

    @GET("asset/{address}/{token}/")
    fun asset(@Path("address") address: String,
              @Path("token") token: String): Observable<OpenSeaAsset>


    data class OpenSeaAsset(@Json(name = "background_color") val backgroundColor: String?,
                            @Json(name = "asset_contract") val assetContract: OpenSeaAssetContract,
                            @Json(name = "owner") val owner: OpenSeaAssetOwner,
                            @Json(name = "image_url") val imageUrl: String)

    data class OpenSeaAssetOwner(val address: String)

    data class OpenSeaAssetContract(val name: String)
}

