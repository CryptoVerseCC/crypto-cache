package io.userfeeds.cryptocache.opensea

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class OpenSeaDecorator(val visitorClass: KClass<*>)