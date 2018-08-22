package io.userfeeds.cryptocache.common

import io.userfeeds.contractmapping.CONTRACTS
import org.springframework.stereotype.Component

@Component
class ContractsProvider {

    fun get(): List<Contract> {
        return CONTRACTS.ALL.map { Contract(it.network, it.address, it.name, it.symbol, it.is721) }
    }
}

data class Contract(
        val network: String,
        val address: String,
        val name: String,
        val symbol: String,
        val is721: Boolean
)