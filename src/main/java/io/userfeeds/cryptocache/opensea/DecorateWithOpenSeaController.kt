package io.userfeeds.cryptocache.opensea

import io.reactivex.Single
import io.userfeeds.cryptocache.apiRetrofit
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
            openSeaItemInterceptor.addOpenSeaData(response.items, Visitor)
        }
    }

    private object Visitor : OpenSeaItemInterceptor.Visitor<MutableMap<String, Any>> {

        override fun visit(item: MutableMap<String, Any>, accept: (MutableMap<String, Any>) -> Unit) {
            accept(item)
            item.likes.forEach(accept)
            item.replies.forEach { visit(item, accept) }
        }
    }

    interface RankingApi {

        @POST("/ranking")
        fun ranking(@Body flow: MutableMap<String, Any>): Single<MutableMap<String, Any>>
    }

    private companion object {

        private val MutableMap<String, Any>.items
            @Suppress("UNCHECKED_CAST")
            get() = this["items"] as List<MutableMap<String, Any>>

        private val MutableMap<String, Any>.likes
            @Suppress("UNCHECKED_CAST")
            get() = this["likes"] as? List<MutableMap<String, Any>> ?: emptyList()

        private val MutableMap<String, Any>.replies
            @Suppress("UNCHECKED_CAST")
            get() = this["replies"] as? List<MutableMap<String, Any>> ?: emptyList()
    }
}
