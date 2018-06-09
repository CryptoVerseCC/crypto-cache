package io.userfeeds

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Viewer(private val store: Store) {

    @RequestMapping("/purrs", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPurrs(@RequestParam("page") page: Int, @RequestParam("size") size: Int): io.userfeeds.Page {
        val totalPages = store.cache.size / size
        val take = store.cache.drop(page * size).take(size)
        return Page(pageNumber = page, totalPages = totalPages, total = store.cache.size, items = take)
    }
}

data class Page(val items: List<Any>, val pageNumber: Int, val totalPages: Int, val total: Int)