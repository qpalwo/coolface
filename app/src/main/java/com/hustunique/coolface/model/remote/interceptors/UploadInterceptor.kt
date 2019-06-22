package com.hustunique.coolface.model.remote.interceptors

import okhttp3.*
import java.io.File

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class UploadInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.body() !is FormBody) {
            return chain.proceed(chain.request())
        }
        val oldBody = request.body() as FormBody
        var upload = -1
        for (i in 0 until oldBody.size()) {
            if (oldBody.value(i).contains("[upload]"))
                upload = i
        }
        if (upload < 0)
            return chain.proceed(chain.request())
        val requestBuilder = request.newBuilder()
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        for (i in 0 until oldBody.size()) {
            if (i != upload) {
                multipartBody.addFormDataPart(oldBody.encodedName(i), oldBody.value(i))
                continue
            }
            val filePath = oldBody.value(i).split("]")
            val file = File(filePath[1])
            multipartBody.addFormDataPart(
                oldBody.encodedName(i),
                file.name,
                RequestBody.create(MediaType.parse("image/jpeg"), file)
            )
        }
        return chain.proceed(requestBuilder.post(multipartBody.build()).build())
    }
}