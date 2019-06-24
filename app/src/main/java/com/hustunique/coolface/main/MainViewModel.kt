package com.hustunique.coolface.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.model.repo.PostRepo

class MainViewModel : ViewModel() {
    private lateinit var postRepo: PostRepo

    val postsData: MutableLiveData<Resource<List<Post>>> = MutableLiveData()

    fun init(context: Context) {
        postRepo = PostRepo.getInstance(context)
        getPosts()
    }

    private fun getPosts() {
        postRepo.getPosts(postsData)
    }

    fun getPictureFile(context: Context) = PictureRepo.getInstance(context).getNewFile()
}