package com.hustunique.coolface.model.remote.bean

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/22/19
 */
data class StarBean(val starUrl: String, val faceToken: String, val starName: String)

data class StarReturnBean(val results: List<StarBean>)
