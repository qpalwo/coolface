package com.hustunique.coolface.showscore

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.model.local.PictureRepo
import com.hustunique.coolface.model.local.ScoringRepo
import com.hustunique.coolface.model.remote.bean.FacePPReturn
import java.io.File

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class ShowScoreViewModel : ViewModel() {
    val scoringData: MutableLiveData<FacePPReturn> = MutableLiveData()
    lateinit var mContext: Context

    fun init(context: Context) {
        mContext = context
    }

    fun getPictureFile() = PictureRepo.getInstance(mContext).getFile()

    fun scoring(picture: File) {
        ScoringRepo.getInstance(mContext).scoring(picture, scoringData)
    }

}