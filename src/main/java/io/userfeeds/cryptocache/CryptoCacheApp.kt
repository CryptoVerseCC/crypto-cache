package io.userfeeds.cryptocache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@SpringBootApplication
@EnableScheduling
class CryptoCacheApp {

    @Bean
    fun taskScheduler(): TaskScheduler = ThreadPoolTaskScheduler().apply { poolSize = 8 }
}

fun main(args: Array<String>) {
    runApplication<CryptoCacheApp>(*args)
}
