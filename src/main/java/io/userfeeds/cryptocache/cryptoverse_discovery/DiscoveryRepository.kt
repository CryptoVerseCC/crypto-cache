package io.userfeeds.cryptocache.cryptoverse_discovery

import io.userfeeds.cryptocache.common.Contract
import io.userfeeds.cryptocache.common.ContractsProvider
import org.springframework.stereotype.Component

enum class Type {
    erc721,
    erc20
}

@Component
class DiscoveryRepository(
        private val contractsProvider: ContractsProvider) {

    val assets = contractsProvider.get().map { it.asset() }

    val discoveries = mutableMapOf<String, Discovery>()

    fun get(asset: String): Discovery? {
        return discoveries[asset]
    }

    fun put(asset: String, discovery: Discovery) {
        discoveries[asset] = discovery
    }
}

private fun Contract.asset(): Pair<String, Type> {
    return "$network:$address" to if (is721) Type.erc721 else Type.erc20
}


data class Discovery(val latest: List<Any>, val twitter: List<Any>, val facebook: List<Any>, val instagram: List<Any>, val github: List<Any>)
