package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import org.springframework.stereotype.Component

@Component
class OpenSeaService(private val api: OpenSeaApi) {

    fun loadData(context: String): Observable<OpenSeaData> {
        val (_, contractAddress, tokenId) = context.split(':')
        return api.loadData(contractAddress, tokenId)
                .map {
                    OpenSeaData(
                            context = context,
                            backgroundColor = it.backgroundColor,
                            externalLink = it.externalLink?.takeIf { it.contains(tokenId) }
                                    ?: "https://opensea.io/assets/$contractAddress/$tokenId",
                            imageUrl = it.imagePreviewUrl,
                            name = it.name
                    )
                }
    }
}
