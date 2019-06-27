package com.hustunique.coolface.main

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
    private lateinit var pictureRepo: PictureRepo

    /**
     * 整个列表更新
     */
    val postsData: MutableLiveData<Resource<List<Post>>> = MutableLiveData()
    val pictureData: MutableLiveData<Resource<String>> = MutableLiveData()

    /**
     * 单个更新的Livedata
     */
    val postData: MutableLiveData<Resource<Post>> = MutableLiveData()

    /**
     * 我的列表
     */
    val myPostsData: MutableLiveData<List<Post>> = MutableLiveData()

    /**
     * 我的收藏
     */
    val collectPostsData: MutableLiveData<List<Post>> = MutableLiveData()

    fun init() {
        postRepo = PostRepo.getInstance()
        user.postValue(BmobUser.getCurrentUser(User::class.java))
        getPosts()
        pictureRepo = PictureRepo.getInstance()
        pictureData.value = Resource.loading()
    }

    fun getPosts() {
        postRepo.getPosts(postsData)
    }

    fun updatePostAt(positon: Int) {
        postRepo.getPost(postsData.value?.data?.get(positon)?.objectId!!, postData)
    }

    /**
     * 显示收藏的动态
     */
    fun updateCollectPosts() {

    }

    /**
     * 显示我的动态
     */
    fun updateMyPosts() {
        myPostsData.postValue(postsData.value?.data?.let {
            it.filter {
                it.username == BmobUser.getCurrentUser(User::class.java).nickname
            }
        })
    }

    fun like(positon: Int, onError: ((String) -> Unit)) {
        postsData.value?.data?.let {
            if (it[positon].likeUser == null) {
                it[positon].likeUser = ArrayList()
            }
            (it[positon].likeUser as MutableList).add(user.value?.username!!)
            postRepo.like(it[positon].objectId!!, null, onError)
        }
    }

    fun unLike(positon: Int, onError: ((String) -> Unit)) {
        postsData.value?.data?.let {
            (it[positon].likeUser as MutableList).apply {
                removeAt(indexOf(user.value?.username))
            }
            postRepo.unLike(it[positon].objectId!!, null, onError)
        }
    }

    fun getPictureFile() = PictureRepo.getInstance().getNewFile()
}