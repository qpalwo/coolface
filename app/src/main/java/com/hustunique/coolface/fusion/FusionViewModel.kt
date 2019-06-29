package com.hustunique.coolface.fusion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Resource

import com.hustunique.coolface.model.repo.PictureRepo
import java.io.File

class FusionViewModel : ViewModel() {
    val mergeResult = MutableLiveData<Resource<String>>()

    fun getNewPictureFile() = PictureRepo.getInstance().getNewFile()

    fun mergeFace(file1: File, file2: File) {
        PictureRepo.getInstance().mergeFace(
            file1.absolutePath, file2.absolutePath
            , mergeResult
        )
    }
}