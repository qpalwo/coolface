package com.hustunique.coolface.bean

import cn.bmob.v3.BmobObject


/**
 * 该类表示一条动态
 * @param imageUrl 照片的url
 * @param message 动态附带的文字
 * @param username 发送该照片的用户名
 * @param likeCount 点赞数
 * @param faceBean 从服务器获取的人脸照片的检测的数据
 */
data class Post(
    var imageUrl: String,
    var message: String,
    var username: String,
    var likeCount: Int,
    var faceBean: FaceBean?
): BmobObject()