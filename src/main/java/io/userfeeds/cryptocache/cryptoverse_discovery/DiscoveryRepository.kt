package io.userfeeds.cryptocache.cryptoverse_discovery

import org.springframework.stereotype.Component

@Component
class DiscoveryRepository {

    val assets = mutableSetOf(
            //"ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d", // CK
            "ethereum:0xf7a6e15dfd5cdd9ef12711bd757a9b6021abf643", // Bots
            "ethereum:0xa6d954d08877f8ce1224f6bfb83484c7d3abf8e9", // Moji
            "ethereum:0x323a3e1693e7a0959f65972f3bf2dfcb93239dfe", // DAC
            "ethereum:0xfa6f7881e52fdf912c4a285d78a3141b089ce859",
            "ethereum:0x108c05cac356d93b351375434101cfd3e14f7e44",
            "ethereum:0x0f5d2fb29fb7d3cfee444a200298f468908cc942",
            "ethereum:0x89205a3a3b2a69de6dbf7f01ed13b2108b2c43e7",
            "ethereum:0x9f8f72aa9304c8b593d555f12ef6589cc3a579a2"
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
