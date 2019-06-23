package com.hustunique.coolface.bean

import com.hustunique.coolface.model.remote.bean.Face
import com.hustunique.coolface.util.JsonUtil
import java.io.Serializable


/**
 * 该类表示一条动态
 * @param imageUrl 照片的url
 * @param message 动态附带的文字
 * @param username 发送该照片的用户名
 * @param likeCount 点赞数
 * @param faceBean 从服务器获取的人脸照片的检测的数据
 */
data class Post(
    var message: String,
    var username: String,
    var userAccount: String,
    var likeCount: Int,
    var faceBean: FaceBean,
    var comments: List<String>? = null
) : Serializable {
    @Transient
    var face: Face? = null
        get() {
            if (field == null) {
                field = JsonUtil.toBean(faceBean.faceInfo)
            }
            return field
        }
}
