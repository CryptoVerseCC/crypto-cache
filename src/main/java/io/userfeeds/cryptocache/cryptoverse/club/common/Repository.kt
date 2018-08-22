package io.userfeeds.cryptocache.cryptoverse.club.common

import io.userfeeds.cryptocache.cryptoverse.main.common.Cache

interface Repository {

    var cache: MutableMap<String, Cache>
}
