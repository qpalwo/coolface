package com.hustunique.coolface.util

import com.squareup.moshi.Moshi
import okio.BufferedSource

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
object JsonUtil {
    fun toJson(bean: Any) =
        Moshi.Builder().build().adapter(bean.javaClass).toJson(bean)

    inline fun <reified T> toBean(json: String): T? = Moshi.Builder().build().adapter<T>(T::class.java).fromJson(json)

    inline fun <reified T> toBean(json: BufferedSource): T? = Moshi.Builder().build().adapter<T>(T::class.java).fromJson(json)

}