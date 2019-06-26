package com.hustunique.coolface.model.remote.bean.bmob

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/24/19
 */
data class BmobUodateObject(
    val __op: String,
    val objects: List<String>
)

data class BmobUpdateAmount(
    val amount: Int,
    val __op: String = "Increment"
)

data class BmobCommonUpdateBean(val comments: BmobUodateObject)

data class BmobLikeCountUpdateBean(
    val likeCount: BmobUpdateAmount,
    val likeUser: BmobUodateObject)

data class BmobFavouriteUpdateBean(val favouriteUser: BmobUodateObject)

