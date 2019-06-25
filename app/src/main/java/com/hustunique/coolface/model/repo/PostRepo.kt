package com.hustunique.coolface.model.repo

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.BmobUser
import com.hustunique.coolface.bean.FaceBean
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.bean.SMMSReturn
import com.hustunique.coolface.model.remote.bean.bmob.*
import com.hustunique.coolface.model.remote.bean.facepp.Face
import com.hustunique.coolface.model.remote.bean.facepp.SimilarFaceInfo
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
class PostRepo private constructor() {
    companion object {
        private lateinit var Instance: PostRepo
        fun getInstance(): PostRepo {
            if (!::Instance.isInitialized) {
                Instance = PostRepo()
            }
            return Instance
        }
    }

    private val smmsService = RetrofitService.Instance.smmsRetrofit.create(SMMSService::class.java)

    private val facePPService = RetrofitService.Instance.facePPRetrofit.create(FacePPService::class.java)

    private val bmobService = RetrofitService.Instance.bombRetrofit.create(BmobService::class.java)

    private val pictureRepo = PictureRepo.getInstance()

    @SuppressLint("CheckResult")
    fun post(message: String, faceData: Face, callback: MutableLiveData<Resource<Post>>) {
        val user = BmobUser.getCurrentUser(User::class.java)
        var post: Post? = null
        var pictureInfo: SMMSReturn? = null
        if (pictureRepo.beautifiedPicture == null) {
            callback.value = Resource.error("no file to postData")
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
                pictureInfo = it
                post = Post(
                    message,
                    //todo change to true user
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
            .flatMap {
                pictureInfo?.data?.let {
                    bmobService.addData(
                        BmobConfig.TABLE_USER,
                        SimilarFaceInfo(
                            it.url,
                            faceData.face_token,
                            "testuser"
                        )
                    )
                }
            }
            .subscribe({
                callback.postValue(Resource.success(post))
            }, {
                callback.postValue(Resource.error("postData error"))
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

    @SuppressLint("CheckResult")
    fun getPost(postObjId: String, liveData: MutableLiveData<Resource<Post>>) {
        liveData.value = Resource.loading()
        bmobService.getData(BmobConfig.TABLE_POST, postObjId)
            .subscribeOn(Schedulers.io())
            .subscribe({
                val post = JsonUtil.toBean<Post>(it.source())
                post?.let {
                    liveData.postValue(Resource.success(post))
                    return@subscribe
                }
                liveData.postValue(Resource.error("load error"))
            }, {
                liveData.postValue(Resource.error("load error"))
            })
    }

    fun addComment(postObjId: String, comment: String, liveData: MutableLiveData<Resource<Post>>? = null, onError: ((String) -> Unit)? = null) {
        liveData?.value = Resource.loading()
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
            ).subscribeOn(Schedulers.io()),
            onError
        )
    }

    fun like(
        postObjId: String,
        liveData: MutableLiveData<Resource<Post>>? = null,
        onError: ((String) -> Unit)? = null
    ) {
        like(postObjId, 1, "AddUnique", liveData, onError)
    }

    fun unLike(postObjId: String, liveData: MutableLiveData<Resource<Post>>?, onError: ((String) -> Unit)? = null) {
        like(postObjId, -1, "Remove", liveData, onError)
    }

    private fun like(
        postObjId: String,
        amount: Int,
        op: String,
        liveData: MutableLiveData<Resource<Post>>?,
        onError: ((String) -> Unit)?
    ) {
        liveData?.value = Resource.loading()
        updatePost(
            postObjId,
            liveData,
            bmobService.updateData(
                BmobConfig.TABLE_POST,
                postObjId,
                BmobLikeCountUpdateBean(
                    BmobUpdateAmount(amount),
                    BmobUodateObject(
                        op,
                        listOf("testuser")
                    )
                    //todo change to true user
                )
            )
                .subscribeOn(Schedulers.io()),
            onError
        )
    }

    @SuppressLint("CheckResult")
    private fun updatePost(
        postObjId: String,
        liveData: MutableLiveData<Resource<Post>>?,
        single: Single<ResponseBody>,
        onError: ((String) -> Unit)? = null
    ) {
        single.flatMap {
            bmobService.getData(
                BmobConfig.TABLE_POST,
                postObjId
            )
        }.subscribe({
            val post = JsonUtil.toBean<Post>(it.source())
            post?.let {
                liveData?.postValue(Resource.success(post))
                return@subscribe
            }
            onError?.run {
                this("postData error")
            }
            liveData?.postValue(Resource.error("postData error"))
        }, {
            onError?.run {
                this("postData error")
            }
            liveData?.postValue(Resource.error("postData error"))
        })
    }
}