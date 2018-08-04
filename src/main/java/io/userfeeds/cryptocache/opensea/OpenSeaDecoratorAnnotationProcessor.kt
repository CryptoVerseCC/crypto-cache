package io.userfeeds.cryptocache.opensea

import io.reactivex.Observable
import io.userfeeds.cryptocache.ContextItem
import io.userfeeds.cryptocache.ItemsWrapper
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.isAccessible

class OpenSeaDecoratorAnnotationProcessor(
        private val openSeaItemInterceptor: OpenSeaItemInterceptor
) : InstantiationAwareBeanPostProcessorAdapter() {

    override fun postProcessAfterInitialization(bean: Any, beanName: String?): Any? {
        val openSeaDecorator = AnnotationUtils.findAnnotation(bean.javaClass, OpenSeaDecorator::class.java)
        if (openSeaDecorator != null) {
            @Suppress("UNCHECKED_CAST")
            openSeaDecorator.visitorClass.constructors.forEach { it.isAccessible = true }
            val visitor = openSeaDecorator.visitorClass.createInstance() as OpenSeaItemInterceptor.Visitor<ContextItem>
            return newProxyInstance(bean.javaClass.classLoader, bean.javaClass.interfaces, DecoratingWithOpenSeaMethodInterceptor(openSeaItemInterceptor, visitor, bean))
        }
        return bean
    }

    private class DecoratingWithOpenSeaMethodInterceptor(private val openSeaInterceptor: OpenSeaItemInterceptor,
                                                         private val itemVisitor: OpenSeaItemInterceptor.Visitor<ContextItem>,
                                                         private val original: Any) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
            val methodResult = callOriginalMethod(method, args)
            return methodResult.map { it.apply { openSeaInterceptor.addOpenSeaData(it.items, itemVisitor) } }
        }

        private fun callOriginalMethod(method: Method, args: Array<out Any>?): Observable<ItemsWrapper> {
            @Suppress("UNCHECKED_CAST")
            return if (args == null)
                method.invoke(original)
            else {
                method.invoke(original, *args)
            } as Observable<ItemsWrapper>
        }
    }
}