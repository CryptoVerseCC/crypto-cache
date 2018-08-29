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

    init {
        logger.info("Cache initialization start...")
        val items = openSeaRepository.findAll()
        cache.putAll(items.map { it.context to Observable.just(it) })
        logger.info("Cache initialization ended")
    }

    fun getData(context: String): Observable<OpenSeaData> {
        return cache.getOrPut(context) { loadDataFromApi(context) }
    }

    private fun loadDataFromApi(context: String): Observable<OpenSeaData> {
        return service.loadData(context)
                .doOnNext {
                    cache[context] = Observable.just(it)
                    openSeaRepository.save(it)
                }
                .share()
    }

    fun update(newItems: List<OpenSeaData>) {
        cache.putAll(newItems.map { it.context to Observable.just(it) })
    }
}
