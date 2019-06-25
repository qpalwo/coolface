package com.hustunique.coolface.showscore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.remote.bean.facepp.Face
import com.hustunique.coolface.model.remote.bean.facepp.SimilarFaceInfo
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.model.repo.PostRepo

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class ShowScoreViewModel : ViewModel() {
    private val pictureRepo = PictureRepo.getInstance()

    private val postRepo = PostRepo.getInstance()

    val scoringData: MutableLiveData<Resource<Face>> = MutableLiveData()

    val pictureData: MutableLiveData<Resource<String>> = MutableLiveData()

    val postData: MutableLiveData<Resource<Post>> = MutableLiveData()

    val similarStarData = MutableLiveData<Resource<SimilarFaceInfo>>()

    val similarUserData = MutableLiveData<Resource<SimilarFaceInfo>>()

    fun init() {
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
                pictureRepo.scoring(it, scoringData)
            } == null) {
            scoringData.value = Resource.error("load file error")
        }
    }

    fun post(message: String) {
        scoringData.value?.data?.let {
            postRepo.post(message, it, postData)
        }
    }

    fun similar() {
        scoringData.value?.data?.let {
            pictureRepo.searchSameStarFace(it.face_token, similarStarData)
            pictureRepo.searchSameUserFace(it.face_token, similarUserData)
        }
    }

}