package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import io.userfeeds.cryptocache.logger
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class OpenSeaCache(
        private val openSeaRepository: OpenSeaRepository,
        private val service: OpenSeaService
) {

    private val cache = ConcurrentHashMap<String, Observable<OpenSeaData>>()
    private val cacheInit = initializeCache()

    fun getData(context: String): Observable<OpenSeaData> {
        return cacheInit.flatMap {
            cache.getOrPut(context) { loadDataFromApi(context) }
        }
    }

    private fun initializeCache(): Observable<Unit> {
        return Observable
                .fromCallable {
                    logger.warn("Cache initialization started!")
                    val items = openSeaRepository.findAll()
                    cache.putAll(items.map { it.context to Observable.just(it) }.toMap())
                }
                .cache()
    }

    private fun loadDataFromApi(context: String): Observable<OpenSeaData> {
        return service.loadData(context)
                .doOnNext {
                    cache[context] = Observable.just(it)
                    openSeaRepository.save(it)
                }
                .share()
    }

    fun invalidate() {
        cache.clear()
    }
}
