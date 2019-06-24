package com.hustunique.coolface.model.remote.bean.facepp

import com.squareup.moshi.Json

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */

data class SimilarFaceInfo(
    val faceUrl: String,
    val faceToken: String,
    val faceOwnerName: String,
    @Transient
    var trustLevel: String = ""
)