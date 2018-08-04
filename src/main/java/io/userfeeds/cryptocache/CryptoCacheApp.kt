package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.opensea.OpenSeaConfig
import io.userfeeds.cryptocache.opensea.OpenSeaDecoratorAnnotationProcessor
import io.userfeeds.cryptocache.opensea.OpenSeaItemInterceptor
import io.userfeeds.cryptocache.retrofit.EnableAutoRetrofit
import io.userfeeds.cryptocache.retrofit.RetrofitAnnotationProcessor
import okhttp3.OkHttpClient
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@SpringBootApplication
@EnableScheduling
@EnableAutoRetrofit
@Import(OpenSeaConfig::class)
class CryptoCacheApp {

    @Bean
    fun taskScheduler(): TaskScheduler = ThreadPoolTaskScheduler().apply { poolSize = 8 }

    @Bean
    @Primary
    fun retrofit() = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(apiBaseUrl)
            .client(OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build())
            .build()

    @Bean
    fun retrofitAnnotationProcessor(beanFactory: AutowireCapableBeanFactory, retrofit: Retrofit) =
            RetrofitAnnotationProcessor(beanFactory, retrofit)

    @Bean
    fun openSeaDecoratorAnnotationProcessor(openSeaItemInterceptor: OpenSeaItemInterceptor) =
            OpenSeaDecoratorAnnotationProcessor(openSeaItemInterceptor)

}

fun main(args: Array<String>) {
    runApplication<CryptoCacheApp>(*args)
}
