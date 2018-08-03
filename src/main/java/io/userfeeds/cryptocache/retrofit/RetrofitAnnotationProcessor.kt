package io.userfeeds.cryptocache.retrofit

import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter
import retrofit2.Retrofit

class RetrofitAnnotationProcessor(private val beanFactory: AutowireCapableBeanFactory,
                                  private val retrofit: Retrofit) : InstantiationAwareBeanPostProcessorAdapter() {

    override fun postProcessBeforeInstantiation(beanClass: Class<*>, beanName: String?): Any? {
        if (beanClass.isAnnotationPresent(AutoRetrofit::class.java)) {
            return retrofit.create(beanClass).also {
                beanFactory.autowireBean(it)
            }
        }
        return null
    }
}