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
        val items = store.cache.dropWhile { if(after != null ) it["id"] as String != after else false }.drop(1).take(size)
        return Page(totalPages = totalPages, total = store.cache.size, items = items)
    }
}

data class Page(val items: List<Any>, val totalPages: Int, val total: Int)