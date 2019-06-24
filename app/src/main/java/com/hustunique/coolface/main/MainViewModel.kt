package com.hustunique.coolface.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobUser
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.model.repo.PostRepo

class MainViewModel : ViewModel() {
    private lateinit var postRepo: PostRepo

    val postsData: MutableLiveData<Resource<List<Post>>> = MutableLiveData()

    fun init(context: Context) {
        postRepo = PostRepo.getInstance(context)
        getPosts()
        user.postValue(BmobUser.getCurrentUser(User::class.java))
    }

    private fun getPosts() {
        postRepo.getPosts(postsData)
    }

    val posts = MutableLiveData<List<Post>>()
    val user = MutableLiveData<User>()

    fun getPictureFile(context: Context) = PictureRepo.getInstance(context).getNewFile()
}