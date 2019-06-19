package com.hustunique.coolface.remote

import com.hustunique.coolface.remote.interceptors.FacePPKeyInterceptor
import okhttp3.OkHttpClient

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
object OkHttpClients {
    val facepp = OkHttpClient.Builder()
        .addInterceptor(FacePPKeyInterceptor())
        .build()
}