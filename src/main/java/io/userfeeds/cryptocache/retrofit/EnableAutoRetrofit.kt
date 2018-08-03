package io.userfeeds.cryptocache.retrofit

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Import(AutoRetrofitInterfaceScanner::class)
annotation class EnableAutoRetrofit(vararg val value: String = [])