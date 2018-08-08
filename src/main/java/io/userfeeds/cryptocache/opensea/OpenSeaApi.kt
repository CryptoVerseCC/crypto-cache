package io.userfeeds.cryptocache.opensea

import com.squareup.moshi.Json
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenSeaApi {

    @GET("asset/{address}/{token}/")
    fun loadData(
            @Path("address") address: String,
            @Path("token") token: String): Observable<OpenSeaDataFromApi>

    data class OpenSeaDataFromApi(
            @Json(name = "background_color") val backgroundColor: String?,
            @Json(name = "external_link") val externalLink: String?,
            @Json(name = "name") val name: String?,
            @Json(name = "image_preview_url") val imagePreviewUrl: String?
    )
}
