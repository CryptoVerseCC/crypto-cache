package io.userfeeds.cryptocache.cryptoverse_discovery

import io.userfeeds.cryptocache.cryptoverse_discovery.Type.erc20
import io.userfeeds.cryptocache.cryptoverse_discovery.Type.erc721
import org.springframework.stereotype.Component

enum class Type {
    erc721,
    erc20
}

@Component
class DiscoveryRepository {

    val assets = mapOf(
            "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d" to erc721, // CK
            "ethereum:0xf5b0a3efb8e8e4c201e2a935f110eaaf3ffecb8d" to erc721, // Axie
            "ethereum:0xf7a6e15dfd5cdd9ef12711bd757a9b6021abf643" to erc721, // Bots
            "ethereum:0xa6d954d08877f8ce1224f6bfb83484c7d3abf8e9" to erc721, // Moji
            "ethereum:0x323a3e1693e7a0959f65972f3bf2dfcb93239dfe" to erc721, // DAC
            "ethereum:0xdde2d979e8d39bb8416eafcfc1758f3cab2c9c72" to erc721, // KO
            "ethereum:0xdcaad9fd9a74144d226dbf94ce6162ca9f09ed7e" to erc721, // Str
            "ethereum:0x4fece400c0d3db0937162ab44bab34445626ecfe" to erc721, // Town
            "ethereum:0x71c118b00759b0851785642541ceb0f4ceea0bd5" to erc721, // Chibi
            "ethereum:0x87d598064c736dd0c712d329afcfaa0ccc1921a1" to erc721, // Fighters
            "ethereum:0xabc7e6c01237e8eef355bba2bf925a730b714d5f" to erc721, // Saga
            "ethereum:0xb2c0782ae4a299f7358758b2d15da9bf29e1dd99" to erc721, // Mons
            "ethereum:0xc70be5b7c19529ef642d16c10dfe91c58b5c3bf0" to erc721, // Myeth
            "ethereum:0x663e4229142a27f00bafb5d087e1e730648314c3" to erc721, // Panda
            "ethereum:0xd26114cd6ee289accf82350c8d8487fedb8a0c07" to erc20, // OMG
            "ethereum:0xfa6f7881e52fdf912c4a285d78a3141b089ce859" to erc20,
            "ethereum:0x108c05cac356d93b351375434101cfd3e14f7e44" to erc20,
            "ethereum:0x0f5d2fb29fb7d3cfee444a200298f468908cc942" to erc20,
            "ethereum:0x89205a3a3b2a69de6dbf7f01ed13b2108b2c43e7" to erc20,
            "ethereum:0x9f8f72aa9304c8b593d555f12ef6589cc3a579a2" to erc20,
            "ethereum:0xc5fdd3e4665b5d7bc4dc9102622d7cb0fe510b65" to erc20, // Sanmari
            "ethereum:0x97d392337cc92564085ac51b99178a371c8bdcf3" to erc20, // Kiyosalo
            "ethereum:0xc93058ca0cc2330b847c001c835fc926fedf5a07" to erc20 // %
    )

    val discoveries = mutableMapOf<String, Discovery>()

    fun get(asset: String): Discovery? {
        return discoveries.get(asset)
    }

    fun put(asset: String, discovery: Discovery) {
        discoveries.put(asset, discovery)
    }
}

data class Discovery(val latest: List<Any>, val twitter: List<Any>, val facebook: List<Any>, val instagram: List<Any>, val github: List<Any>)
