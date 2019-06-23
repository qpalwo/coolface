package com.hustunique.coolface.model.remote.service

import com.hustunique.coolface.model.remote.config.NetConfig
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

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
    fun queryData(@Path("table") tableName: String,
                @Query("where") op: String): Single<ResponseBody>

    @GET("${NetConfig.BMOB_METHOD_CLASS}{table}")
    fun getData(@Path("table") tableName: String): Single<ResponseBody>

    @GET("${NetConfig.BMOB_METHOD_CLASS}{table}")
    fun getData(@Path("table") tableName: String,
                @Query("limit") limit: Int,
                @Query("skip") skip: Int): Single<ResponseBody>
}