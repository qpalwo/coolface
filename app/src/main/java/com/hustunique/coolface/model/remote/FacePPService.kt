package com.hustunique.coolface.model.remote

import com.hustunique.coolface.model.remote.bean.FacePPBeautifyReturn
import com.hustunique.coolface.model.remote.bean.FacePPDetectReturn
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
               @Field("return_attributes") attr: String): Single<FacePPDetectReturn>

    @POST(NetConfig.FACEPP_METHOD_BEAUTIFY)
    @FormUrlEncoded
    fun beautify(@Field("image_file") picture_path: String,
                 @Field("whitening") whitening: Int,
                 @Field("smoothing") smoothing: Int): Single<FacePPBeautifyReturn>
}