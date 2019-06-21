package com.hustunique.coolface.model.remote

import com.hustunique.coolface.model.remote.bean.FacePPBeautifyReturn
import com.hustunique.coolface.model.remote.bean.FacePPDetectReturn
import com.hustunique.coolface.model.remote.bean.FacePPSetAddReturn
import com.hustunique.coolface.model.remote.bean.FacePPSetDeleteReturn
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

    @POST(NetConfig.FACEPP_METHOD_ADDTO_FACESET)
    @FormUrlEncoded
    fun setAddFace(@Field("faceset_token") facesetToken: String,
                   @Field("face_tokens") faceToken: String): Single<FacePPSetAddReturn>

    @POST(NetConfig.FACEPP_METHOD_REMOVEFROM_FACESET)
    @FormUrlEncoded
    fun setRemoveFace(@Field("faceset_token") facesetToken: String,
                   @Field("face_tokens") faceToken: String): Single<FacePPSetDeleteReturn>
}