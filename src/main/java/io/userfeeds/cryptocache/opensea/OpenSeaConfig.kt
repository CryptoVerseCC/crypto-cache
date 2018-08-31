package io.userfeeds.cryptocache.opensea

import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Configuration
class OpenSeaConfig {

    @Bean(name = ["openSea"])
    fun retrofit(moshiConverterFactory: MoshiConverterFactory): Retrofit = Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.from(Executors.newFixedThreadPool(10))))
            .client(OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor {
                        it.proceed(it.request().newBuilder().header("X-API-KEY", "51a17355a4574864949683e0217647d6").build())
                    }
                    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS })
                    .build())
            .baseUrl("https://api.opensea.io/api/v1/")
            .build()

    @Bean
    fun api(@Qualifier("openSea") retrofit: Retrofit): OpenSeaApi = retrofit.create(OpenSeaApi::class.java)
}
