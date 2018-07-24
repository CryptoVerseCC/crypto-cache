package io.userfeeds.cryptocache

import org.slf4j.LoggerFactory

val Any.logger
    get() = LoggerFactory.getLogger(javaClass)
