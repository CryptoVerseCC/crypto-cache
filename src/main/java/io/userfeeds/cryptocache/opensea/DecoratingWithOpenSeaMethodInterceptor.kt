package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import io.userfeeds.cryptocache.ItemsWrapper
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class DecoratingWithOpenSeaMethodInterceptor(private val openSeaInterceptor: OpenSeaItemInterceptor) : MethodInterceptor {
    override fun intercept(p0: Any?, p1: Method?, p2: Array<out Any>?, p3: MethodProxy?): Any {
        return Observable.error<ItemsWrapper>(RuntimeException("kasper"))
    }
}