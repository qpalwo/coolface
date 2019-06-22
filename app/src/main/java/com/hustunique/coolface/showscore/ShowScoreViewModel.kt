package com.hustunique.coolface.showscore

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.remote.bean.Face
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.model.repo.PostRepo

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class ShowScoreViewModel : ViewModel() {
    private lateinit var pictureRepo: PictureRepo

    private lateinit var postRepo: PostRepo

    val scoringData: MutableLiveData<Resource<Face>> = MutableLiveData()

    val pictureData: MutableLiveData<Resource<String>> = MutableLiveData()

    val postData: MutableLiveData<Resource<Post>> = MutableLiveData()

    lateinit var mContext: Context


    fun init(context: Context) {
        mContext = context
        pictureRepo = PictureRepo.getInstance(mContext)
        postRepo = PostRepo.getInstance(mContext)
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

    fun test() {
        val livedata = MutableLiveData<Resource<List<Post>>>()
        postRepo.getPosts(livedata)
    }

}