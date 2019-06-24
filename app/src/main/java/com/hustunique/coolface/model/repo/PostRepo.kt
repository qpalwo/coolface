package com.hustunique.coolface.model.repo

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.BmobUser
import com.hustunique.coolface.bean.FaceBean
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.bean.bmob.*
import com.hustunique.coolface.model.remote.bean.facepp.Face
import com.hustunique.coolface.model.remote.config.BmobConfig
import com.hustunique.coolface.model.remote.config.FacePPConfig
import com.hustunique.coolface.model.remote.service.BmobService
import com.hustunique.coolface.model.remote.service.FacePPService
import com.hustunique.coolface.model.remote.service.SMMSService
import com.hustunique.coolface.util.JsonUtil
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

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

    private val bmobService = RetrofitService.Instance.bombRetrofit.create(BmobService::class.java)

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
                post = Post(
                    message,
                    "testuser",
                    "test_account",
                    0,
                    FaceBean(
                        faceData.face_token,
                        it.data.url,
                        it.data.delete,
                        JsonUtil.toJson(faceData)
                    ),
                    ArrayList()
                )
                bmobService.addData(BmobConfig.TABLE_POST, post!!)
            }
            .subscribe({
                callback.postValue(Resource.success(post))
            }, {
                callback.postValue(Resource.error("post error"))
            })

    }

    @SuppressLint("CheckResult")
    fun getPosts(liveData: MutableLiveData<Resource<List<Post>>>) {
        liveData.value = Resource.loading()
        bmobService.getData(BmobConfig.TABLE_POST)
            .subscribeOn(Schedulers.io())
            .subscribe({
                val list = JsonUtil.toBean<BmobPostsReturn>(it.source())
                if (list?.let {
                        liveData.postValue(Resource.success(it.results))
                    } == null) {
                    liveData.postValue(Resource.success(ArrayList()))
                }
            }, {
                liveData.postValue(Resource.error("get data error"))
            })
    }

    fun addComment(postObjId: String, comment: String, liveData: MutableLiveData<Resource<Post>>) {
        liveData.value = Resource.loading()
        updatePost(
            postObjId,
            liveData,
            bmobService.updateData(
                BmobConfig.TABLE_POST,
                postObjId,
                BmobCommonUpdateBean(
                    BmobUodateObject(
                        "AddUnique",
                        listOf(comment)
                    )
                )
            ).subscribeOn(Schedulers.io())
        )
    }

    fun like(postObjId: String, liveData: MutableLiveData<Resource<Post>>) {
        like(postObjId, 1, liveData)
    }

    fun unLike(postObjId: String, liveData: MutableLiveData<Resource<Post>>) {
        like(postObjId, -1, liveData)
    }

    private fun like(postObjId: String, amount: Int, liveData: MutableLiveData<Resource<Post>>) {
        liveData.value = Resource.loading()
        updatePost(
            postObjId,
            liveData,
            bmobService.updateData(
                BmobConfig.TABLE_POST,
                postObjId,
                BmobLikeCountUpdateBean(
                    BmobUpdateAmount(amount)
                )
            ).subscribeOn(Schedulers.io())
        )
    }

    @SuppressLint("CheckResult")
    private fun updatePost(postObjId: String, liveData: MutableLiveData<Resource<Post>>, single: Single<ResponseBody>) {
        single.flatMap {
            bmobService.getData(
                BmobConfig.TABLE_POST,
                postObjId
            )
        }
            .subscribe({
                val post = JsonUtil.toBean<Post>(it.source())
                post?.let {
                    liveData.postValue(Resource.success(post))
                    return@subscribe
                }
                liveData.postValue(Resource.error("post error"))
            }, {
                liveData.postValue(Resource.error("post error"))
            })
    }
}