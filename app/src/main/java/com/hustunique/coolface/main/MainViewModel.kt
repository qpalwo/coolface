package com.hustunique.coolface.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.model.repo.PostRepo
import com.hustunique.coolface.util.LiveDataUtil

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

    val posts: MutableList<Post> = ArrayList()

    /**
     * 整个列表更新
     */
    val postsData: MutableLiveData<Resource<List<Post>>> = MutableLiveData()
    val pictureData: MutableLiveData<Resource<String>> = MutableLiveData()

    /**
     * 单个更新的Livedata
     */
    val postData: MutableLiveData<Resource<Post>> = MutableLiveData()

    fun init(activity: MainActivity) {
        postRepo = PostRepo.getInstance()
        user.value = BmobUser.getCurrentUser(User::class.java)
        getPosts(activity)
        pictureRepo = PictureRepo.getInstance()
        pictureData.value = Resource.loading()
    }

    fun updatePosts(activity: MainActivity, status: Int = this.status, onSuccess: () -> Unit = {}) {
        when (status) {
            0 -> {
                getPosts(activity, onSuccess)
            }
            1 -> {
                updateMyPosts()
                onSuccess.invoke()
            }
            2 -> {
                updateCollectPosts()
                onSuccess.invoke()
            }
        }
    }

    private fun getPosts(activity: MainActivity, onSuccess: () -> Unit = {}) {
        status = 0
        postRepo.getPosts(MutableLiveData<Resource<List<Post>>>().apply {
            observe(activity, Observer {
                LiveDataUtil.useData(it, { postsList ->
                    posts.removeAll(posts)
                    posts.addAll(postsList!!)
                    postsData.postValue(Resource.success(posts))
                    onSuccess.invoke()
                })
            })
        })
    }

    fun updatePostAt(position: Int) {
        if (position < postsData.value?.data?.size!!) {
            val targetPostId: String = postsData.value?.data?.get(position)?.objectId!!
            postRepo.getPost(targetPostId, postData)
        }
    }

    /**
     * 显示收藏的动态
     */
    private fun updateCollectPosts() {
        status = 2
        postsData.postValue(Resource.success(
            posts.filter {
                it.favouriteUser?.contains(BmobUser.getCurrentUser(User::class.java).username) ?: false
            }
        ))
    }

    fun deleteAt(position: Int, onSuccess:() -> (Unit),onLoading: () -> (Unit), onError: (String) -> (Unit)) {
        if (status == 1) {
            onLoading.invoke()
            postRepo.deletePost(postsData.value?.data?.get(position)?.objectId!!, {
                posts.remove(postsData.value?.data?.get(position)!!)
                when (status) {
                    0 -> postsData.postValue(Resource.success(posts))
                    1 -> updateMyPosts()
                    2 -> updateCollectPosts()
                }
                onSuccess.invoke()
            }, onError)
        }
    }

    /**
     * 显示我的动态
     */
    private fun updateMyPosts() {
        status = 1
        postsData.postValue(Resource.success(posts.filter {
            it.username == BmobUser.getCurrentUser(User::class.java).nickname
        }))
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