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
    val user = MutableLiveData<User>()
    private lateinit var postRepo: PostRepo

    val postsData: MutableLiveData<Resource<List<Post>>> = MutableLiveData()

    fun init(context: Context) {
        postRepo = PostRepo.getInstance(context)
        user.postValue(BmobUser.getCurrentUser(User::class.java))
        getPosts()
    }

    private fun getPosts() {
        postRepo.getPosts(postsData)
    }

    fun like(positon: Int) {
        postsData.value?.data?.let {
//            postRepo.like(positon, postsData)
        }
    }

    fun unLike(positon: Int) {
        postsData.value?.data?.let {
        }
    }

    fun getPictureFile(context: Context) = PictureRepo.getInstance(context).getNewFile()
}