package com.hustunique.coolface.model.remote

import com.hustunique.coolface.model.remote.config.NetConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
class RetrofitService private constructor() {
    companion object {
        val Instance = RetrofitService()
    }

    val facePPRetrofit = Retrofit.Builder()
        .baseUrl(NetConfig.FACEPP_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(OkHttpClients.facepp)
        .build()

    val smmsRetrofit = Retrofit.Builder()
        .baseUrl(NetConfig.SMMS_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(OkHttpClients.smms)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    val bombRetrofit = Retrofit.Builder()
        .baseUrl(NetConfig.BMOB_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(OkHttpClients.bmob)
        .build()
}