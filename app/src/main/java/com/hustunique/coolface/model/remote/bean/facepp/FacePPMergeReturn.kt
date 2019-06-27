package com.hustunique.coolface.model.remote.bean.facepp

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/27/19
 */
data class FacePPMergeReturn(
    val error_message: String,
    val request_id: String,
    val result: String,
    val time_used: Int
)