package com.hustunique.coolface.model.local

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.remote.FacePPService
import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.SMMSService
import com.hustunique.coolface.model.remote.bean.FacePPDetectReturn
import com.hustunique.coolface.util.FacePPAttrUtil
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import top.zibin.luban.Luban
import java.io.File

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class ScoringRepo private constructor(val context: Context) {

    companion object {
        private lateinit var Instance: ScoringRepo
        fun getInstance(context: Context): ScoringRepo {
            if (!::Instance.isInitialized) {
                Instance = ScoringRepo(context)
            }
            return Instance
        }
    }

    val facePPService = RetrofitService.Instance.facePPRetrofit.create(FacePPService::class.java)
    val smmsService = RetrofitService.Instance.smmsRetrofit.create(SMMSService::class.java)

    @SuppressLint("CheckResult")
    fun scoring(picture: File, liveData: MutableLiveData<Resource<FacePPDetectReturn>>) {
        Single.just(picture)
            .subscribeOn(Schedulers.io())
            .flatMap {
                val compressedFile = Luban.with(context)
                    .load(it)
                    .get()
                facePPService.detect("[upload]${compressedFile[0].absolutePath}", FacePPAttrUtil.Builder().default())
            }
            .subscribe({
                liveData.postValue(Resource.success(it))
            }, {
                liveData.postValue(Resource.error(it.message ?: ""))
            })
    }
}