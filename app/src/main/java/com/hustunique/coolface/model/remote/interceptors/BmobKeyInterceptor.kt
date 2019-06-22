package com.hustunique.coolface.model.remote.interceptors

import com.hustunique.coolface.model.remote.config.BmobConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
class BmobKeyInterceptor : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("X-Bmob-Application-Id", BmobConfig.APPLICATION_ID)
                .addHeader("X-Bmob-REST-API-Key", BmobConfig.REST_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build()
        )
    }
}