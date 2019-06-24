package com.hustunique.coolface

import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.bean.bmob.BmobCommonUpdateBean
import com.hustunique.coolface.model.remote.bean.bmob.BmobLikeCountUpdateBean
import com.hustunique.coolface.model.remote.bean.bmob.BmobUodateObject
import com.hustunique.coolface.model.remote.bean.bmob.BmobUpdateAmount
import com.hustunique.coolface.model.remote.config.BmobConfig
import com.hustunique.coolface.model.remote.service.BmobService
import com.hustunique.coolface.model.remote.service.SMMSService
import com.hustunique.coolface.util.JsonUtil
import org.junit.Test
import retrofit2.HttpException

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
class InterfaceTest {

    val bmobService = RetrofitService.Instance.bombRetrofit.create(BmobService::class.java)

    val smmsService = RetrofitService.Instance.smmsRetrofit.create(SMMSService::class.java)

    @Test
    fun bmob() {
//        bmobService.queryData(BmobConfig.TABLE_STAR, "{\"faceToken\":\"4df46a5844040bbb3fcf9568fbe6f86c\"}")
//            .subscribe({
//                val info = JsonUtil.toBean<BmobSimilarFaceReturn>(it.source())
//                val a = 0
//            }, {
//                val b = 1
//            })
        bmobService.updateData(
            BmobConfig.TABLE_POST,
            "1f9e9c50ea",
            BmobLikeCountUpdateBean(
                BmobUpdateAmount(-1)
            )
        )
            .subscribe({
                val a = 0
            }, {
                val a = it as HttpException
                a.response().errorBody()?.let {
                    val s = it.string()
                }
                val b = 1
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