package io.userfeeds.cryptocache.common

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

@Component
class ContractsProvider(moshiConverterFactory: MoshiConverterFactory) {
    private val contractMapping = Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.from(Executors.newFixedThreadPool(10))))
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }).build())
            .baseUrl("https://raw.githubusercontent.com")
            .build()
            .create(ContractMapping::class.java)

    private val contracts: AtomicReference<List<Contract>> = AtomicReference(fetchCurrentContracts())

    @GetMapping("update_contract_mapping")
    fun update(): String {
        try {
            contracts.set(fetchCurrentContracts())
            return "Patryk wszystko poszło ok"
        } catch (e: Exception) {
            return "Patryk coś zjebałeś. Pokaż to Maćkowi jak nie wiesz czemu nie działa ${e.message ?: e.toString()}"
        }
    }

    private fun fetchCurrentContracts(): List<Contract> {
        val currentContracts = contractMapping.getCurrentMapping().blockingGet().values.toList()
        currentContracts.forEach {
            if (it.address != it.address.toLowerCase()) {
                throw RuntimeException("Patryk!!! miało być lowercasem co to jest ${it.address} w ${it.name} zmień na ${it.address.toLowerCase()}")
            }
        }
        return currentContracts
    }

    fun get(): List<Contract> {
        return contracts.get()
    }
}

interface ContractMapping {
    @GET("CryptoVerseCC/contract-mapping/master/mapping.json")
    fun getCurrentMapping(): Single<Map<String, Contract>>
}

data class Contract(
        val network: String,
        val address: String,
        val name: String,
        val symbol: String,
        val is721: Boolean
) {

    val asset: String
        get() = "$network:$address"

    val type: Type
        get() = if (is721) Type.erc721 else Type.erc20

    enum class Type {
        erc721,
        erc20
    }
}