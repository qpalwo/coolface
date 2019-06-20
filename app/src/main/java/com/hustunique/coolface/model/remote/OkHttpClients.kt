package com.hustunique.coolface.model.remote

import com.hustunique.coolface.model.remote.interceptors.FacePPKeyInterceptor
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