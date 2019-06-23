package com.hustunique.coolface.model.remote.service

import com.hustunique.coolface.model.remote.config.NetConfig
import com.hustunique.coolface.model.remote.bean.SMMSReturn
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
interface SMMSService {
    @FormUrlEncoded
    @POST(NetConfig.SMMS_METHOD_UPLOAD)
    fun upload(@Field("smfile") picturePath: String): Single<SMMSReturn>

    @GET("${NetConfig.SMMS_METHOD_DELETE}{key}")
    fun delete(@Path("key") key: String): Single<ResponseBody>


}