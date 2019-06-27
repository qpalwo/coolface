package com.hustunique.coolface.model.remote.service

import com.hustunique.coolface.model.remote.bean.facepp.*
import com.hustunique.coolface.model.remote.config.NetConfig
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
interface FacePPService {

    @POST(NetConfig.FACEPP_METHOD_DETECT)
    @FormUrlEncoded
    fun detect(
        @Field("image_file") picture_path: String,
        @Field("return_attributes") attr: String
    ): Single<FacePPDetectReturn>

    @POST(NetConfig.FACEPP_METHOD_DETECT)
    @FormUrlEncoded
    fun detect(@Field("image_url") picture_path: String): Single<FacePPDetectReturn>

    @POST(NetConfig.FACEPP_METHOD_BEAUTIFY)
    @FormUrlEncoded
    fun beautify(
        @Field("image_file") picture_path: String,
        @Field("whitening") whitening: Int,
        @Field("smoothing") smoothing: Int
    ): Single<FacePPBeautifyReturn>

    @POST(NetConfig.FACEPP_METHOD_ADDTO_FACESET)
    @FormUrlEncoded
    fun setAddFace(
        @Field("faceset_token") facesetToken: String,
        @Field("face_tokens") faceToken: String
    ): Single<FacePPSetAddReturn>

    @POST(NetConfig.FACEPP_METHOD_REMOVEFROM_FACESET)
    @FormUrlEncoded
    fun setRemoveFace(
        @Field("faceset_token") facesetToken: String,
        @Field("face_tokens") faceToken: String
    ): Single<FacePPSetDeleteReturn>

    @POST(NetConfig.FACEPP_METHOD_SEARCH)
    @FormUrlEncoded
    fun searchSimilarFace(
        @Field("face_token") faceToken: String,
        @Field("faceset_token") facesetToken: String,
        @Field("return_result_count") count: Int
    ): Single<FacePPSearchSimilarReturn>

    @POST(NetConfig.FACEPP_MEtHOD_MERGE)
    @FormUrlEncoded
    fun mergeFace(
        @Field("template_url") templatePicture: String,
        @Field("merge_file") mergePicture: String,
        @Field("merge_rate") mergeRate: Int
    ): Single<FacePPMergeReturn>
}