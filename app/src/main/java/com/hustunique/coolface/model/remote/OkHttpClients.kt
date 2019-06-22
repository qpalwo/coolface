package com.hustunique.coolface.model.remote

import android.webkit.WebSettings
import com.hustunique.coolface.model.remote.interceptors.BmobKeyInterceptor
import com.hustunique.coolface.model.remote.interceptors.FacePPKeyInterceptor
import com.hustunique.coolface.model.remote.interceptors.UploadInterceptor
import okhttp3.OkHttpClient

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
object OkHttpClients {
    val facepp = OkHttpClient.Builder()
        .addInterceptor(FacePPKeyInterceptor())
        .addInterceptor(UploadInterceptor())
        .build()

    val smms = OkHttpClient.Builder()
        .addInterceptor {
            it.run {
                proceed(
                    it.request()
                        .newBuilder()
                        .removeHeader("User-Agent")//移除旧的
                        .addHeader("User-Agent", "Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5")
                        .build()
                )
            }
        }
        .addInterceptor(UploadInterceptor())
        .build()

    val bmob = OkHttpClient.Builder()
        .addInterceptor(BmobKeyInterceptor())
        .build()
}