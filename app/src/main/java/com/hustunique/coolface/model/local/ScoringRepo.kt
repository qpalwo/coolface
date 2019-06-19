package com.hustunique.coolface.model.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.hustunique.coolface.model.remote.bean.FacePPReturn
import top.zibin.luban.Luban
import java.io.File

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class ScoringRepo private constructor(val context: Context){

    companion object {
        private lateinit var Instance: ScoringRepo
        fun getInstance(context: Context): ScoringRepo {
            if (!::Instance.isInitialized) {
                Instance = ScoringRepo(context)
            }
            return Instance
        }
    }

    fun scoring(picture: File, liveData: MutableLiveData<FacePPReturn>) {
        Luban.with(context)
            .load(picture)
            .launch()
    }
}