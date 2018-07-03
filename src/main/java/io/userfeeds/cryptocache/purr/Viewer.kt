package io.userfeeds.cryptocache.purr

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Viewer(private val store: Store) {

    @RequestMapping("/purrs")
    fun getPurrs(@RequestParam("after", required = false) after: String?,
                 @RequestParam("size", required = false) size: Int?,
                 @RequestParam("before", required = false) before: String?,
                 @RequestParam("catId", required = false) catId: String?): Page {
        val allItems = store.cache
        val filteredItems = if (catId != null) filterByCatId(allItems, catId) else filterOutLikes(allItems)
        val items = when {
            after != null && size != null -> getItemsAfter(after, size, filteredItems)
            before != null -> getItemsBefore(before, filteredItems)
            size != null -> getXItems(size, filteredItems)
            else -> throw IllegalArgumentException("After, size  or before must be provided!")
        }
        val totalPages = if (size != null) filteredItems.size / size else 1
        return Page(totalPages = totalPages, total = filteredItems.size, items = items)
    }

    private fun filterOutLikes(items: List<Map<String, Any>>): List<Map<String, Any>> {
        return items.filter { it["type"] as String != "like" }
    }

    private fun filterByCatId(items: List<Map<String, Any>>, catId: String): List<Map<String, Any>> {
        return items.filter { it["context"] as String == catId || (it["about"] as? Map<String, Any>)?.get("id") == catId }
    }

    private fun getItemsBefore(before: String?, items: List<Map<String, Any>>) = items.takeWhile { it["id"] as String != before }

    private fun getXItems(size: Int, items: List<Map<String, Any>>) = items.take(size)

    private fun getItemsAfter(after: String?, size: Int, items: List<Map<String, Any>>) =
            items.dropWhile { it["id"] as String != after }.drop(1).take(size)
}

data class Page(val items: List<Any>, val totalPages: Int, val total: Int)
