package io.userfeeds.cryptocache.common

import io.reactivex.Observable
import io.userfeeds.cryptocache.ItemsWrapper

interface Api {

    fun getFeed(): Observable<ItemsWrapper>
}
