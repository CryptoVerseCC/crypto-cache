package io.userfeeds

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CachePurrApp

fun main(args: Array<String>) {
    runApplication<CachePurrApp>(*args)
}

