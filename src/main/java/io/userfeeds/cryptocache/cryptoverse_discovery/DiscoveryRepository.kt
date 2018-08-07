package io.userfeeds.cryptocache.cryptoverse_discovery

import io.userfeeds.contractmapping.CONTRACTS
import io.userfeeds.contractmapping.Contract
import org.springframework.stereotype.Component

enum class Type {
    erc721,
    erc20
}

@Component
class DiscoveryRepository {

    val assets = mapOf(
            CONTRACTS.CRYPTOKITTIES.asset(),
            CONTRACTS.AXIES.asset(),
            CONTRACTS.CRYPTOBOTS.asset(),
            CONTRACTS.ETH_MOJI.asset(),
            CONTRACTS.DIGITAL_ART_CHAIN.asset(),
            CONTRACTS.KNOWN_ORIGIN.asset(),
            CONTRACTS.CRYPTO_STRIKERS.asset(),
            CONTRACTS.ETH_TOWN.asset(),
            CONTRACTS.CHIBI_FIGHTERS.asset(),
            CONTRACTS.CRYPTO_FIGHTERS.asset(),
            CONTRACTS.CRYPTO_SAGA.asset(),
            CONTRACTS.ETHEREMON.asset(),
            CONTRACTS.MYTHEREUM.asset(),
            CONTRACTS.PANDA_EARTH.asset(),
            CONTRACTS.CRYPTO_COWS.asset(),
            CONTRACTS.CRYPTO_VOXELS.asset(),
            CONTRACTS.BASIC_ATTENTION_TOKEN.asset(),
            CONTRACTS.OMNISE_GO.asset(),
            CONTRACTS.GOLEM.asset(),
            CONTRACTS.STATUS.asset(),
            CONTRACTS.ZRX.asset(),
            CONTRACTS.AVOCADO.asset(),
            CONTRACTS.BENTYN.asset(),
            CONTRACTS.DECENTRALAND_MANA.asset(),
            CONTRACTS.UNICORN.asset(),
            CONTRACTS.MAKER.asset(),
            CONTRACTS.SANMARICOIN.asset(),
            CONTRACTS.KIYOSALO.asset(),
            CONTRACTS.DDGT.asset(),
            CONTRACTS.TOKEN_X.asset(),
            CONTRACTS.PERCENT.asset()
    )

    val discoveries = mutableMapOf<String, Discovery>()

    fun get(asset: String): Discovery? {
        return discoveries.get(asset)
    }

    fun put(asset: String, discovery: Discovery) {
        discoveries.put(asset, discovery)
    }
}

private fun Contract.asset(): Pair<String, Type> {
    return "$network:$address" to if (is721) Type.erc721 else Type.erc20
}


data class Discovery(val latest: List<Any>, val twitter: List<Any>, val facebook: List<Any>, val instagram: List<Any>, val github: List<Any>)
