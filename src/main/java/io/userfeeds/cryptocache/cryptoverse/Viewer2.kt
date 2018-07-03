package io.userfeeds.cryptocache.cryptoverse

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Viewer2(private val repository: Repository) {

    @RequestMapping("/cryptoverse_feed")
    fun getFeed(
            @RequestParam("before", required = false) before: String?,
            @RequestParam("after", required = false) after: String?,
            @RequestParam("size", required = false) size: Int?): Page {
        val allItems = repository.cache
        val items = allItems
                .runIf(before != null) { takeWhile { it["id"] as String != before } }
                .runIf(after != null) { dropWhile { it["id"] as String != after }.drop(1) }
                .runIf(size != null) { take(size!!) }
        val totalPages = if (size != null) allItems.size / size else null
        return Page(items = items, totalPages = totalPages, total = allItems.size)
    }
}

inline fun <T> T.runIf(condition: Boolean, block: T.() -> T): T {
    return if (condition) block() else this
}

data class Page(val items: List<Any>, val totalPages: Int?, val total: Int)
