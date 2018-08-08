package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import io.userfeeds.cryptocache.ItemsWrapper
import io.userfeeds.cryptocache.retrofit.AutoRetrofit
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import retrofit2.http.Body
import retrofit2.http.POST

@RestController
class DecorateWithOpenSeaController(private val api: RankingApi) {

    @PostMapping("/decorate_with_opensea")
    @CrossOrigin("*")
    fun decorateWithOpenSea(@RequestBody flow: MutableMap<String, Any>): ItemsWrapper {
        return api.ranking(flow).blockingFirst()
    }

    private class Visitor : OpenSeaItemInterceptor.Visitor<MutableMap<String, Any>> {

        override fun visit(item: MutableMap<String, Any>, accept: (MutableMap<String, Any>) -> Unit) {
            accept(item)
            item.likes.forEach(accept)
            item.replies.forEach { visit(it, accept) }
        }
    }

    @AutoRetrofit
    @OpenSeaDecorator(Visitor::class)
    interface RankingApi {

        @POST("/ranking")
        fun ranking(@Body flow: MutableMap<String, Any>): Observable<ItemsWrapper>
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
