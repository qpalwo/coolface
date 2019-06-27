package com.hustunique.coolface.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
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
     * 表示现在在哪个状态下
     * 0：所有动态
     * 1：我的
     * 2：收藏
     */
    var status = 0

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
        user.value = BmobUser.getCurrentUser(User::class.java)
        getPosts()
        pictureRepo = PictureRepo.getInstance()
        pictureData.value = Resource.loading()
    }

    fun getPosts() {
        status = 0
        postRepo.getPosts(postsData)
    }

    fun updatePostAt(positon: Int) {
        val targetPostId: String =
            when (status) {
                0 -> {
                    postsData.value?.data?.get(positon)?.objectId!!
                }
                1 -> {
                    myPostsData.value?.get(positon)?.objectId!!
                }
                2 -> {
                    collectPostsData.value?.get(positon)?.objectId!!
                }
                else -> {
                    ""
                }
            }
        postRepo.getPost(targetPostId, postData)
    }

    /**
     * 显示收藏的动态
     */
    fun updateCollectPosts() {
        collectPostsData.postValue(postsData.value?.data?.let {
            status = 2
            it.filter {
                it.favouriteUser?.contains(BmobUser.getCurrentUser(User::class.java).username) ?: false
            }
        })
    }

    fun deleteAt(post: Post) {

    }

    /**
     * 显示我的动态
     */
    fun updateMyPosts() {
        myPostsData.postValue(postsData.value?.data?.let {
            status = 1
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

    fun upLoadAvatar(onError: (String) -> Unit) {
        pictureRepo.uploadUserAvatar({ url ->
            user.value?.let {
                it.avatar = url
                it.update(object : UpdateListener() {
                    override fun done(p0: BmobException?) {
                        if (p0 != null) {
                            onError(p0.errorCode.toString())
                        }
                    }
                })
            }
        }, onError)
    }

    fun getNewPictureFile() = PictureRepo.getInstance().getNewFile()

    fun getPictureFile() = pictureRepo.getFile()
}