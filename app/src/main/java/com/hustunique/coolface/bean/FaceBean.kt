package com.hustunique.coolface.bean

import cn.bmob.v3.BmobObject

/**
 * 该类表示人脸分析所得的数据
 */
class FaceBean(
    val faceToken: String,
    val faceUrl: String,
    val faceDeleteKey: String,
    val faceInfo: String
): BmobObject()
