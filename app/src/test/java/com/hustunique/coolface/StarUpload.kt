package com.hustunique.coolface

import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.bean.SimilarFaceInfo
import com.hustunique.coolface.model.remote.config.BmobConfig
import com.hustunique.coolface.model.remote.config.FacePPConfig
import com.hustunique.coolface.model.remote.service.BmobService
import com.hustunique.coolface.model.remote.service.FacePPService
import okio.buffer
import okio.source
import org.junit.Test
import java.io.File

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
class StarUpload {

    val facePPService = RetrofitService.Instance.facePPRetrofit.create(FacePPService::class.java)

    val bmobService = RetrofitService.Instance.bombRetrofit.create(BmobService::class.java)

    @Test
    fun run() {
        val file = File("star_img.txt")
        val buffer = file.source().buffer()
        while (buffer.readUtf8Line()?.let {
                val strings = it.split("\t")
                facePPService.detect(strings[1])
                    .subscribe({ face ->
                        bmobService.addData(
                            BmobConfig.TABLE_STAR,
                            SimilarFaceInfo(strings[1], face.faces[0].face_token, strings[0])
                        ).subscribe({
                            facePPService.setAddFace(FacePPConfig.STAR_FACE_SET_TOKEN, face.faces[0].face_token)
                                .subscribe({
                                    val a = 0
                                }, {
                                    val a = 0
                                })
                        }, {
                            val a = 0
                        })

                    }, {
                        val b = 0
                    })
            } != null) {
        }
        buffer.close()
    }
}