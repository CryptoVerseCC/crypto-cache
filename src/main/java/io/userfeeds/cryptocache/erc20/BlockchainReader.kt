package io.userfeeds.cryptocache.erc20

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.userfeeds.parityapi.ParityApiFactoryImpl
import io.userfeeds.parityapi.getStringAtIndex
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class BlockchainReader(
        @Value("\${PARITY_HOST}") parityHost: String,
        @Value("\${PARITY_PORT}") parityPort: String) {

    private val api = ParityApiFactoryImpl().createEthModuleApi("http://$parityHost:$parityPort")

    fun getTokenInfo(asset: String): Single<TokenInfo> {
        check(asset.startsWith("ethereum:"))
        val address = asset.removePrefix("ethereum:")
        return Single.zip(
                api.call(address, "0x06fdde03" /* name */),
                api.call(address, "0x95d89b41" /* symbol */),
                BiFunction { name, symbol ->
                    val safeName = if (name.length >= 130) name.getStringAtIndex(0) else ""
                    val safeSymbol = if (symbol.length >= 130) symbol.getStringAtIndex(0) else ""
                    TokenInfo(asset, safeName, safeSymbol)
                }
        )
    }
}
