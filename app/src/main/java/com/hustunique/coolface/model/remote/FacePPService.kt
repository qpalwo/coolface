package com.hustunique.coolface.model.remote

import com.hustunique.coolface.model.remote.bean.FacePPReturn
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
interface FacePPService {

    @POST(NetConfig.facePPMethodDetect)
    @FormUrlEncoded
    fun detect(@Field("image_base64") image: String,
               @Field("return_attributes") attr: String): Single<FacePPReturn>
}