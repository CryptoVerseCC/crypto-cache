package io.userfeeds.cryptocache.opensea

import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter
import org.springframework.cglib.proxy.Enhancer
import org.springframework.core.annotation.AnnotationUtils

class OpenSeaDecoratorAnnotationProcessor(
        private val methodInterceptor: DecoratingWithOpenSeaMethodInterceptor
) : InstantiationAwareBeanPostProcessorAdapter() {

    override fun postProcessAfterInitialization(bean: Any, beanName: String?): Any? {
        if (AnnotationUtils.findAnnotation(bean.javaClass, OpenSeaDecorator::class.java) != null) {
            return Enhancer.create(bean.javaClass.interfaces.first(), methodInterceptor)
        }
        return bean
    }
}