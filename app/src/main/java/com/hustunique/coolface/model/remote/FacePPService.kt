package com.hustunique.coolface.model.remote

import com.hustunique.coolface.model.remote.bean.FacePPReturn
import io.reactivex.Single
import retrofit2.http.*

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
interface FacePPService {

    @POST(NetConfig.FACEPP_METHOD_DETECT)
    @FormUrlEncoded
    fun detect(@Field("image_file") picture_path: String,
               @Field("return_attributes") attr: String): Single<FacePPReturn>
}