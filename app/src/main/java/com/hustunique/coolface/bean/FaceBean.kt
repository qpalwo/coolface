package com.hustunique.coolface.bean

import cn.bmob.v3.BmobObject

/**
 * 该类表示人脸分析所得的数据
 */
class FaceBean(
    /**
     * face++的token：确认人脸
     */
    val faceToken: String,
    /**
     * 图床的url
     */
    val faceUrl: String,
    /**
     * 删图床用的
     */
    val faceDeleteKey: String,
    /**
     * face的信息：json格式
     */
    val faceInfo: String
): BmobObject()
