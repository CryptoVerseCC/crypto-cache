package io.userfeeds.cryptocache.cryptoverse_discovery

import org.springframework.stereotype.Component

@Component
class DiscoveryRepository {

    val assets = mutableSetOf(
            "ethereum:0xfa6f7881e52fdf912c4a285d78a3141b089ce859",
            "ethereum:0x108c05cac356d93b351375434101cfd3e14f7e44",
            "ethereum:0x0f5d2fb29fb7d3cfee444a200298f468908cc942",
            "ethereum:0x89205a3a3b2a69de6dbf7f01ed13b2108b2c43e7"
    )

    val discoveries = mutableMapOf<String, Discovery>()

    fun get(asset: String): Discovery? {
        return discoveries.get(asset)
    }

    fun put(asset: String, discovery: Discovery) {
        assets.add(asset)
        discoveries.put(asset, discovery)
    }
}

data class Discovery(val latest: List<Any>, val twitter: List<Any>, val facebook: List<Any>, val instagram: List<Any>, val github: List<Any>)
