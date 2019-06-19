package com.hustunique.coolface.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.model.local.PictureRepo
import com.hustunique.coolface.model.remote.PostService
import com.hustunique.coolface.util.Callback

class MainViewModel : ViewModel() {
    fun init() {
        PostService.getAllPost(object : Callback<List<Post>> {
            override fun onResponse(result: List<Post>?) {
                if (result != null) {
                    posts.postValue(result)
                } else {
                    posts.postValue(null)
                }
            }
        })
    }

    val posts = MutableLiveData<List<Post>>()

    fun getPictureFile(context: Context) = PictureRepo.getInstance(context).getFile()
}