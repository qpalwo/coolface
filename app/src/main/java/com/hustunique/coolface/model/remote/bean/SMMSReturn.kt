package com.hustunique.coolface.model.remote.bean

data class SMMSReturn(
    val data: Data,
    val code: String,
    val msg: String
)

data class Data(
    val delete: String,
    val filename: String,
    val hash: String,
    val height: Int,
    val ip: String,
    val path: String,
    val size: Int,
    val storename: String,
    val timestamp: Int,
    val url: String,
    val width: Int
)