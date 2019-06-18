package com.hustunique.coolface.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
class FacePPKeyInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}