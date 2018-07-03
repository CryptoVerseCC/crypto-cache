package io.userfeeds.cryptocache.cryptoverse

import org.springframework.stereotype.Component

@Component
class Repository {

    var cache: List<Map<String, Any>> = emptyList()
}
