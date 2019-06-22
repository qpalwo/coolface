package com.hustunique.coolface.model.repo

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import com.hustunique.coolface.bean.FaceBean
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.bean.Face
import com.hustunique.coolface.model.remote.config.FacePPConfig
import com.hustunique.coolface.model.remote.service.FacePPService
import com.hustunique.coolface.model.remote.service.SMMSService
import com.hustunique.coolface.util.JsonUtil
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/21/19
 */
class PostRepo private constructor(val context: Context) {
    companion object {
        private lateinit var Instance: PostRepo
        fun getInstance(context: Context): PostRepo {
            if (!::Instance.isInitialized) {
                Instance = PostRepo(context)
            }
            return Instance
        }
    }

    private val smmsService = RetrofitService.Instance.smmsRetrofit.create(SMMSService::class.java)

    private val facePPService = RetrofitService.Instance.facePPRetrofit.create(FacePPService::class.java)

    private val pictureRepo = PictureRepo.getInstance(context)

    @SuppressLint("CheckResult")
    fun post(message: String, faceData: Face, callback: MutableLiveData<Resource<Post>>) {
        val user = BmobUser.getCurrentUser(User::class.java)
        var post: Post? = null
        if (pictureRepo.beautifiedPicture == null) {
            callback.value = Resource.error("no file to post")
            return
        } else {
            callback.value = Resource.loading()
        }
        facePPService.setAddFace(FacePPConfig.USER_FACE_SET_TOKEN, faceData.face_token)
            .subscribeOn(Schedulers.io())
            .flatMap {
                smmsService.upload("[upload]${pictureRepo.beautifiedPicture!!.absolutePath}")
            }
            .flatMap {
                val faceBean = FaceBean(
                    faceData.face_token,
                    it.data.url,
                    it.data.delete,
                    JsonUtil.toJson(faceData)
                )
//                post = Post(
//                    message,
//                    user.nickname,
//                    user.username,
//                    0, faceBean
                post = Post(
                    message,
                    "testuser",
                    "test_account",
                    0, faceBean
                )
                post?.run {
//                    user.apply {
//                        posts.add(post!!)
//                        updateSync()
//                    }
                    updateObservable()
                        .singleOrError()
                }
                Single.just(post)
            }
            .subscribe({
                callback.postValue(Resource.success(it))
            }, {
                callback.postValue(Resource.error("post error"))
            })

    }

    @SuppressLint("CheckResult")
    fun getPosts(liveData: MutableLiveData<Resource<List<Post>>>) {
        liveData.value = Resource.loading()
        BmobQuery<Post>().findObjectsObservable(Post::class.java)
            .subscribeOn(Schedulers.io())
            .subscribe({
                liveData.postValue(Resource.success(it))
            }, {
                liveData.postValue(Resource.error("load error"))
            })
    }

}