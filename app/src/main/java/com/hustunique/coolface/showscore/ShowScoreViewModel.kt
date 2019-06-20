package com.hustunique.coolface.showscore

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.local.PictureRepo
import com.hustunique.coolface.model.local.ScoringRepo
import com.hustunique.coolface.model.remote.bean.FacePPReturn

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class ShowScoreViewModel : ViewModel() {
    val scoringData: MutableLiveData<Resource<FacePPReturn>> = MutableLiveData()
    lateinit var mContext: Context

    fun init(context: Context) {
        mContext = context
    }

    fun getPictureFile() = PictureRepo.getInstance(mContext).getFile()

    fun scoring() {
        if (getPictureFile()?.let {
                scoringData.value = Resource.loading()
                ScoringRepo.getInstance(mContext).scoring(it, scoringData)
            } == null) {
            scoringData.value = Resource.error("load file error")
        }
    }

}