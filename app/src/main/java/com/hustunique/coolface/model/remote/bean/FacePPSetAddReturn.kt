package com.hustunique.coolface.model.remote.bean

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
data class FacePPSetAddReturn(
    val error_message: String,
    val face_added: Int,
    val face_count: Int,
    val faceset_token: String,
    val failure_detail: List<Any>,
    val outer_id: String,
    val request_id: String,
    val time_used: Int
)