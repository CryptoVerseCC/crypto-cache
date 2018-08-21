package io.userfeeds.cryptocache.cryptoverse.club.common

import io.userfeeds.cryptocache.common.Cache

interface Repository {

    var cache: MutableMap<String, Cache>
}
