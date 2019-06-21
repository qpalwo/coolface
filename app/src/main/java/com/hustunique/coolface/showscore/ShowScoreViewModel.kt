package com.hustunique.coolface.showscore

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.model.repo.ScoringRepo
import com.hustunique.coolface.model.remote.bean.FacePPDetectReturn

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class ShowScoreViewModel : ViewModel() {
    private lateinit var pictureRepo: PictureRepo

    private lateinit var scoringRepo: ScoringRepo

    val scoringData: MutableLiveData<Resource<FacePPDetectReturn>> = MutableLiveData()

    val pictureData: MutableLiveData<Resource<String>> = MutableLiveData()

    lateinit var mContext: Context


    fun init(context: Context) {
        mContext = context
        pictureRepo = PictureRepo.getInstance(mContext)
        scoringRepo = ScoringRepo.getInstance(mContext)

        pictureData.value = Resource.loading()
        if (getPictureFile()?.let {
                pictureRepo.beautify(it.absolutePath, pictureData)
            } == null) {
            pictureData.value = Resource.error("get file error")
        }
    }

    fun getPictureFile() = pictureRepo.getFile()

    fun scoring() {
        if (getPictureFile()?.let {
                scoringData.value = Resource.loading()
                scoringRepo.scoring(it, scoringData)
            } == null) {
            scoringData.value = Resource.error("load file error")
        }
    }

}