package io.userfeeds.cryptocache.opensea

import io.reactivex.Single
import io.userfeeds.cryptocache.apiRetrofit
import io.userfeeds.cryptocache.context
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import retrofit2.http.Body
import retrofit2.http.POST

@RestController
class DecorateWithOpenSeaController(private val openSeaItemInterceptor: OpenSeaItemInterceptor) {

    private val api by lazy {
        apiRetrofit().create(RankingApi::class.java)
    }

    @PostMapping("/decorate_with_open_sea")
    fun addOpenSea(@RequestBody flow: MutableMap<String, Any>): MutableMap<String, Any> {
        return api.ranking(flow).blockingGet().also { response ->
            openSeaItemInterceptor.addOpenSeaData(response.getItemss(), ::Visitor, Extractor)
        }
    }
    class Visitor(private val openSeaDataByContext: Map<String, OpenSeaData>) : OpenSeaDataAddingVisitor<MutableMap<String, Any>> {
        override fun visitItem(item: MutableMap<String, Any>) {
            addContextInfo(item)
            item.getLikess().forEach(this::addContextInfo)
            item.getRepliess().forEach(this::visitItem)
        }

        private fun addContextInfo(item: MutableMap<String, Any>) {
            openSeaDataByContext[item.context]?.let { it ->
                item["context_info"] = ContextInfoApiModel(it)
            }
        }
    }

    object Extractor : ItemIdExtractor<MutableMap<String, Any>> {
        override fun extractContextsFromItem(item: MutableMap<String, Any>): List<String> {
            return (listOf(item.getContext()) + item.getLikess().map { it.getContext() } + item.getRepliess().flatMap { extractContextsFromItem(it) }).filterNotNull()
        }
    }

    interface RankingApi {
        @POST("/ranking")
        fun ranking(@Body flow: MutableMap<String, Any>): Single<MutableMap<String, Any>>
    }

    private companion object {
        @Suppress("UNCHECKED_CAST")
        private fun MutableMap<String, Any>.getContext() = this["context"] as? String

        @Suppress("UNCHECKED_CAST")
        private fun MutableMap<String, Any>.getItemss() = this["items"] as? List<MutableMap<String, Any>> ?: emptyList()

        @Suppress("UNCHECKED_CAST")
        private fun MutableMap<String, Any>.getLikess() = this["likes"] as? List<MutableMap<String, Any>> ?: emptyList()

        @Suppress("UNCHECKED_CAST")
        private fun MutableMap<String, Any>.getRepliess() = this["replies"] as? List<MutableMap<String, Any>>
                ?: emptyList()
    }
}

