package io.userfeeds.cryptocache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CrypotCacheApp

fun main(args: Array<String>) {
    runApplication<CrypotCacheApp>(*args)
}
