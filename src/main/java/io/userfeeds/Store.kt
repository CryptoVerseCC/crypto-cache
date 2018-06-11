package io.userfeeds

import org.springframework.stereotype.Component

@Component
class Store {
    var cache: List<Map<String, Any>> = emptyList()
}
