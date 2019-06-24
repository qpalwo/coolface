package com.hustunique.coolface.model.remote.bean.facepp

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
data class FacePPSetDeleteReturn(
    val error_message: String,
    val face_count: Int,
    val face_removed: Int,
    val faceset_token: String,
    val failure_detail: List<Any>,
    val outer_id: String,
    val request_id: String,
    val time_used: Int
)