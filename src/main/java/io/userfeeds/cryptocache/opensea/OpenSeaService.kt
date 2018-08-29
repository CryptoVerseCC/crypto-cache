package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import org.springframework.stereotype.Component

@Component
class OpenSeaService(private val api: OpenSeaApi) {

    fun loadData(context: String): Observable<OpenSeaData> {
        val (_, contractAddress, tokenId) = context.split(':')
        return api.loadData(contractAddress, tokenId)
                .retry(3)
                .map {
                    OpenSeaData(
                            context = context,
                            backgroundColor = it.backgroundColor,
                            externalLink = it.externalLink?.takeIf { it.contains(tokenId) }
                                    ?: "https://opensea.io/assets/$contractAddress/$tokenId",
                            imageUrl = it.imageUrl?.takeIf { it.endsWith(".svg") }
                                    ?: it.imagePreviewUrl,
                            name = it.name
                    )
                }
                .onErrorReturnItem(OpenSeaData(
                        context = context,
                        backgroundColor = null,
                        externalLink = "https://tokntalk.club/404",
                        imageUrl = null,
                        name = null
                ))
    }
}
