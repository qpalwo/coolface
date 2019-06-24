package com.hustunique.coolface.model.remote.bean.facepp

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/20/19
 */
data class FacePPBeautifyReturn(
    val error_message: String,
    val result: String,
    val time_used: Int
)