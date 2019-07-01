package com.hustunique.coolface.pk

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.remote.bean.facepp.Face
import com.hustunique.coolface.model.repo.PictureRepo

class PkViewModel : ViewModel() {
    val scoringData: MutableLiveData<Resource<Face>> = MutableLiveData()

    fun getNewPictureFile() = PictureRepo.getInstance().getNewFile()

    fun getPictureFile() = PictureRepo.getInstance().getFile()

    fun scoring() {
        if (getPictureFile()?.let {
                scoringData.value = Resource.loading()
                PictureRepo.getInstance().scoring(it, scoringData)
            } == null) {
            scoringData.value = Resource.error("load file error")
        }
    }
}