package com.hustunique.coolface

import com.hustunique.coolface.bean.FaceBean
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.service.BmobService
import com.hustunique.coolface.model.remote.service.SMMSService
import com.hustunique.coolface.util.JsonUtil
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
class InterfaceTest {

    val bmobService = RetrofitService.Instance.bombRetrofit.create(BmobService::class.java)

    val smmsService = RetrofitService.Instance.smmsRetrofit.create(SMMSService::class.java)

    @Test
    fun bmob() {
        val post = Post("url", "message", "name", 0, FaceBean("token", "url", "key", "info"))
//         bmobService.addData("Post", post)
//             .subscribe({
//                 val a = 0;
//             }, {
//                 val b = 0
//             })
        bmobService.getData("Post", "3fa9735c24")
            .subscribe({
                val post = JsonUtil.toBean<Post>(it.source())
                val a = 0
            }, {
                val a = 0
            })
    }

    @Test
    fun smms() {
        smmsService.upload("[upload]/home/xyx/Pictures/Screenshot_20190622_002148.jpg")
            .subscribe({
                val a = 0
            }, {
                val b = 0
            })
    }
}