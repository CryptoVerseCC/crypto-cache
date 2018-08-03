package io.userfeeds.cryptocache

import io.userfeeds.cryptocache.opensea.DecoratingWithOpenSeaMethodInterceptor
import io.userfeeds.cryptocache.opensea.OpenSeaDecoratorAnnotationProcessor
import io.userfeeds.cryptocache.retrofit.EnableAutoRetrofit
import io.userfeeds.cryptocache.retrofit.RetrofitAnnotationProcessor
import okhttp3.OkHttpClient
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@SpringBootApplication
@EnableScheduling
@EnableAutoRetrofit(value = ["io.userfeeds"])
class CryptoCacheApp {

    @Bean
    fun taskScheduler(): TaskScheduler = ThreadPoolTaskScheduler().apply { poolSize = 8 }

    @Bean
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
    fun openSeaDecoratorAnnotationProcessor(methodInterceptor: DecoratingWithOpenSeaMethodInterceptor) =
            OpenSeaDecoratorAnnotationProcessor(methodInterceptor)

}

fun main(args: Array<String>) {
    runApplication<CryptoCacheApp>(*args)
}
