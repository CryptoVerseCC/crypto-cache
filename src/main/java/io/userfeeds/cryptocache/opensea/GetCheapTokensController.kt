package io.userfeeds.cryptocache.opensea

import io.userfeeds.cryptocache.ItemsWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GetCheapTokensController(private val api: OpenSeaApi) {

    @GetMapping("/cheap_tokens")
    fun cheapTokens(@RequestParam("id") id: String): ItemsWrapper {
        val (ethereum, contractAddress) = id.split(":")
        check(ethereum == "ethereum")
        val response = api.cheapTokens(contractAddress).blockingSingle()
        val withSellOrders = response.assets.filter { it.sellOrders?.size ?: 0 > 0 }
        val items = withSellOrders.map {
            val tokenId = it.tokenId
            val context = "ethereum:$contractAddress:$tokenId"
            mutableMapOf(
                    "context" to context,
                    "context_info" to ContextInfoApiModel(
                            background_color = it.backgroundColor,
                            external_link = it.externalLink?.takeIf { it.contains(tokenId) }
                                    ?: "https://opensea.io/assets/$contractAddress/$tokenId",
                            image_preview_url = it.imageUrl?.takeIf { it.endsWith(".svg") }
                                    ?: it.imagePreviewUrl,
                            name = it.name
                    ),
                    "price" to it.currentPrice!!,
                    "sell_order" to it.sellOrders!![0]
            )
        }
        return ItemsWrapper(items)
    }
}
