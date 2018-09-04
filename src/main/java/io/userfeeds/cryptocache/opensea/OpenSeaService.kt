package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import io.reactivex.Observable.interval
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class OpenSeaService(private val api: OpenSeaApi) {

    fun loadData(context: String): Observable<OpenSeaData> {
        val (_, contractAddress, tokenId) = context.split(':')
        return api.loadData(contractAddress, tokenId)
                .retry(2)
                .map {
                    OpenSeaData(
                            context = context,
                            backgroundColor = it.backgroundColor,
                            externalLink = it.externalLink?.takeIf { it.contains(tokenId) }
                                    ?: "https://opensea.io/assets/$contractAddress/$tokenId",
                            imageUrl = it.imageUrl?.takeIf { it.endsWith(".svg") }
                                    ?: it.imagePreviewUrl,
                            name = it.name
                    )
                }
                .onErrorReturnItem(OpenSeaData(
                        context = context,
                        backgroundColor = null,
                        externalLink = "https://tokntalk.club/404",
                        imageUrl = null,
                        name = null
                ))
    }

    fun loadDataMultiple(contexts: List<String>): Single<List<OpenSeaData>> {
        return contexts
                .filter { it.startsWith("ethereum:") }
                .shuffled()
                .groupBy { it.split(":")[1] }
                .flatMap { it.value.chunked(100) }
                .toObservable()
                .flatMap { batch ->
                    val contractAddress = batch[0].split(":")[1]
                    val tokens = batch.map { it.substringAfterLast(":") }
                    api.loadDataMultiple(contractAddress, tokens)
                            .flatMapIterable {
                                it.assets.map {
                                    val tokenId = it.tokenId
                                    OpenSeaData(
                                            context = "ethereum:$contractAddress:$tokenId",
                                            backgroundColor = it.backgroundColor,
                                            externalLink = it.externalLink?.takeIf { it.contains(tokenId) }
                                                    ?: "https://opensea.io/assets/$contractAddress/$tokenId",
                                            imageUrl = it.imageUrl?.takeIf { it.endsWith(".svg") }
                                                    ?: it.imagePreviewUrl,
                                            name = it.name
                                    )
                                }
                            }
                            .onErrorResumeNext(
                                    batch.toObservable()
                                            .zipWith(interval(2, TimeUnit.SECONDS))
                                            .flatMap { loadData(it.first) }
                            )
                }
                .toList()
    }
}
