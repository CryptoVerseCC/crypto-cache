package io.userfeeds.cryptocache.cryptoverse.discovery

import org.springframework.stereotype.Component

@Component
class DiscoveryRepository {

    val discoveries = mutableMapOf<String, Discovery>()

    fun get(asset: String): Discovery? {
        return discoveries[asset]
    }

    fun put(asset: String, discovery: Discovery) {
        discoveries[asset] = discovery
    }
}


data class Discovery(val latest: List<Any>, val twitter: List<Any>, val facebook: List<Any>, val instagram: List<Any>, val github: List<Any>)
