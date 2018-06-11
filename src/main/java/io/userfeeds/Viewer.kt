package io.userfeeds

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Viewer(private val store: Store) {

    @RequestMapping("/purrs")
    @CrossOrigin(origins = ["*"])
    fun getPurrs(@RequestParam("after", required = false) after: String?,
                 @RequestParam("size", required = false) size: Int?,
                 @RequestParam("before", required = false) before: String?): Page {
        val items = when {
            after != null && size != null -> getItemsAfter(after, size)
            before != null -> getItemsBefore(after)
            size != null -> getXItems(size)
            else -> throw IllegalArgumentException("After, size  or before must be provided!")
        }
        val totalPages = if (size != null) store.cache.size / size else 1
        return Page(totalPages = totalPages, total = store.cache.size, items = items)
    }

    private fun getItemsBefore(after: String?) = store.cache.reversed().takeWhile { it["id"] as String != after }

    private fun getXItems(size: Int) = store.cache.take(size)

    private fun getItemsAfter(after: String?, size: Int) =
            store.cache.dropWhile { it["id"] as String != after }.drop(1).take(size)
}

data class Page(val items: List<Any>, val totalPages: Int, val total: Int)