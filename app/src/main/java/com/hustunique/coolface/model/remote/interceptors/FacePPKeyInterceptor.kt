package com.hustunique.coolface.model.remote.interceptors

import com.hustunique.coolface.model.remote.config.FacePPConfig
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
class FacePPKeyInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        val formBody = FormBody.Builder()
            .add("api_key", FacePPConfig.KEY)
            .add("api_secret", FacePPConfig.SECRET)
        if (request.body() is FormBody) {
            val oldBody = request.body() as FormBody
            for (i in 0 until oldBody.size()) {
                formBody.add(oldBody.encodedName(i), oldBody.value(i))
            }
        }
        return chain.proceed(requestBuilder.post(formBody.build()).build())
    }
}