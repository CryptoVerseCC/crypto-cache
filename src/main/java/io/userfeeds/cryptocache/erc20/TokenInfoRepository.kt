package io.userfeeds.cryptocache.erc20

import io.reactivex.rxkotlin.toObservable
import org.springframework.stereotype.Component

@Component
class TokenInfoRepository(private val blockchainReader: BlockchainReader) {

    private val cache = mutableMapOf<String, TokenInfo>()

    fun get(ids: Set<String>): Map<String, TokenInfo> {
        val missing = ids - cache.keys
        val tokenInfos = missing.toObservable()
                .flatMapSingle(blockchainReader::getTokenInfo)
                .map { it.asset to it }
                .toList()
                .blockingGet()
        cache.putAll(tokenInfos)
        return cache.filterKeys { it in ids }
    }
}
