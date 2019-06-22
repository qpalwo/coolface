package com.hustunique.coolface.model.remote.service

import com.hustunique.coolface.model.remote.config.NetConfig
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
interface BmobService {

    @POST("${NetConfig.BMOB_METHOD_CLASS}{table}")
    fun addData(@Path("table") tableName: String,
                @Body info: Any): Single<ResponseBody>


    @GET("${NetConfig.BMOB_METHOD_CLASS}{table}/{objid}")
    fun getData(@Path("table") tableName: String,
                @Path("objid") objId: String): Single<ResponseBody>

    @GET("${NetConfig.BMOB_METHOD_CLASS}{table}")
    fun getData(@Path("table") tableName: String): Single<ResponseBody>
}