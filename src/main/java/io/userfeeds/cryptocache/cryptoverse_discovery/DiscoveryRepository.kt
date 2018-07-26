package io.userfeeds.cryptocache.cryptoverse_discovery

import org.springframework.stereotype.Component

@Component
class DiscoveryRepository {

    val assets = mutableSetOf(
            "ethereum:0xfa6f7881e52fdf912c4a285d78a3141b089ce859"/*,
            "ethereum:0xe41d2489571d322189246dafa5ebde1f4699f498",
            "ethereum:0xd26114cd6ee289accf82350c8d8487fedb8a0c07"*/
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
