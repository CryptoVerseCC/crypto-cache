package io.userfeeds.cryptocache.opensea

import org.springframework.stereotype.Component

@Component
class OpenSeaService(private val api: OpenSeaApi) {

    fun data(asset: Asset) = api.asset(asset.address, asset.token)
            .map {
                OpenSeaData(
                        asset = asset,
                        backgroundColor = it.backgroundColor,
                        externalLink = it.externalLink?.takeIf { it.contains(asset.token) }
                                ?: "https://opensea.io/assets/${asset.address}/${asset.token}",
                        imageUrl = it.imageUrl,
                        name = it.name
                )
            }
}
