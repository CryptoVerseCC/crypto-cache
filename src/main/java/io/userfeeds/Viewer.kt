package io.userfeeds

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Viewer(private val store: Store) {

    @RequestMapping("/purrs", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPurrs(@RequestParam("after", required = false) after: String?, @RequestParam("size") size: Int): Page {
        val totalPages = store.cache.size / size
        val items = getItems(after, size)
        return Page(totalPages = totalPages, total = store.cache.size, items = items)
    }

    private fun getItems(after: String?, size: Int) =
            if (after != null) {
                store.cache.dropWhile { it["id"] as String != after }.drop(1).take(size)
            } else {
                store.cache.take(size)
            }
}

data class Page(val items: List<Any>, val totalPages: Int, val total: Int)