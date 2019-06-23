package com.hustunique.coolface

import com.hustunique.coolface.bean.FaceBean
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.bean.BmobSimilarFaceReturn
import com.hustunique.coolface.model.remote.bean.SimilarFaceInfo
import com.hustunique.coolface.model.remote.bean.StarReturnBean
import com.hustunique.coolface.model.remote.config.BmobConfig
import com.hustunique.coolface.model.remote.service.BmobService
import com.hustunique.coolface.model.remote.service.SMMSService
import com.hustunique.coolface.util.JsonUtil
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
        bmobService.queryData(BmobConfig.TABLE_STAR, "{\"faceToken\":\"4df46a5844040bbb3fcf9568fbe6f86c\"}")
            .subscribe({
                val info = JsonUtil.toBean<BmobSimilarFaceReturn>(it.source())
                val a = 0
            }, {
                val b = 1
            })
//        for (i in 1 .. 12) {
//            bmobService.getData(BmobConfig.TABLE_STAR, 50, i * 50)
//                .subscribe({
//                    val post = JsonUtil.toBean<StarReturnBean>(it.source())
//                    post!!.results.forEach {
//                        val bean = SimilarFaceInfo(it.starUrl, it.faceToken, it.starName)
//                        bmobService.addData("similar_star", bean)
//                            .subscribe()
//                    }
//                }, {
//                    val a = 0
//                })
//        }
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