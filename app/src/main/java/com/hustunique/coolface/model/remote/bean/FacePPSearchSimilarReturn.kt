package com.hustunique.coolface.model.remote.bean

import com.squareup.moshi.Json

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/22/19
 */
data class FacePPSearchSimilarReturn(
    val error_message: String,
    val request_id: String,
    val results: List<Result>,
    val thresholds: Thresholds,
    val time_used: Int
)

data class Result(
    val confidence: Double,
    val face_token: String,
    val user_id: String
)

data class Thresholds(
    @Json(name = "1e-3")
    val e3: Double,
    @Json(name = "1e-4")
    val e4: Double,
    @Json(name = "1e-5")
    val e5: Double
)