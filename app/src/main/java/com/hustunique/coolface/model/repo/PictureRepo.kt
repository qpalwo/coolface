package com.hustunique.coolface.model.repo

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.hustunique.coolface.base.CoolFaceApplication
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.bean.bmob.BmobSimilarFaceReturn
import com.hustunique.coolface.model.remote.bean.facepp.Face
import com.hustunique.coolface.model.remote.bean.facepp.FacePPSearchSimilarReturn
import com.hustunique.coolface.model.remote.bean.facepp.SimilarFaceInfo
import com.hustunique.coolface.model.remote.config.BeautifyLevel
import com.hustunique.coolface.model.remote.config.BmobConfig
import com.hustunique.coolface.model.remote.config.FacePPConfig
import com.hustunique.coolface.model.remote.service.BmobService
import com.hustunique.coolface.model.remote.service.FacePPService
import com.hustunique.coolface.model.remote.service.SMMSService
import com.hustunique.coolface.util.FacePPAttrUtil
import com.hustunique.coolface.util.FileUtil
import com.hustunique.coolface.util.JsonUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okio.buffer
import okio.sink
import top.zibin.luban.Luban
import java.io.File
import java.util.*

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class PictureRepo private constructor() {
    companion object {
        private lateinit var Instance: PictureRepo
        fun getInstance(): PictureRepo {
            if (!::Instance.isInitialized) {
                Instance = PictureRepo()
            }
            return Instance
        }
    }

    private var imageFile: File? = FileUtil.createImageFile(CoolFaceApplication.mApplicationContext)

    var beautifiedPicture: File? = null

    var mergedPicture: File? = null

    private val facePPService = RetrofitService.Instance.facePPRetrofit.create(FacePPService::class.java)

    private val bmobService = RetrofitService.Instance.bombRetrofit.create(BmobService::class.java)

    private val smmsService = RetrofitService.Instance.smmsRetrofit.create(SMMSService::class.java)

    fun getFile() = imageFile

    fun getNewFile(): File? {
        imageFile = FileUtil.createImageFile(CoolFaceApplication.mApplicationContext)
        return imageFile
    }

    @SuppressLint("CheckResult")
    fun beautify(picture: String, pictureData: MutableLiveData<Resource<String>>) {
        Single.just(picture)
            .subscribeOn(Schedulers.io())
            .flatMap {
                val compressedFile = Luban.with(CoolFaceApplication.mApplicationContext)
                    .load(it)
                    .get()
                facePPService.beautify(
                    "[upload]${compressedFile[0].absolutePath}",
                    BeautifyLevel.whitening,
                    BeautifyLevel.smoothing
                )
            }
            .map { data ->
                beautifiedPicture = FileUtil.createImageFile(CoolFaceApplication.mApplicationContext)
                beautifiedPicture?.let {
                    val sink = it.sink().buffer()
                    sink.write(Base64.getDecoder().decode(data.result))
                    it.absolutePath
                }
            }
            .subscribe({
                if (it?.let {
                        pictureData.postValue(Resource.success(it))
                    } == null) {
                    pictureData.postValue(Resource.error("load error"))
                }
            }, {
                pictureData.postValue(Resource.error(it.message ?: ""))
            })

    }

    @SuppressLint("CheckResult")
    fun mergeFace(templatePicture: String, mergePicture: String, pictureData: MutableLiveData<Resource<String>>) {
        Single.just(mergePicture)
            .subscribeOn(Schedulers.io())
            .flatMap {
                val mergeFile = Luban.with(CoolFaceApplication.mApplicationContext)
                    .load(it)
                    .get()
                val templateFile = Luban.with(CoolFaceApplication.mApplicationContext)
                    .load(templatePicture)
                    .get()
                facePPService.mergeFace(
                    "[upload]${templateFile[0].absolutePath}",
                    "[upload]${mergeFile[0].absolutePath}",
                    BeautifyLevel.mergeRate
                )
            }
            .map { data ->
                mergedPicture = FileUtil.createImageFile(CoolFaceApplication.mApplicationContext)
                mergedPicture?.let {
                    val sink = it.sink().buffer()
                    sink.write(Base64.getDecoder().decode(data.result))
                    it.absolutePath
                }
            }
            .subscribe({
                if (it?.let {
                        pictureData.postValue(Resource.success(it))
                    } == null) {
                    pictureData.postValue(Resource.error("merge error"))
                }
            }, {
                pictureData.postValue(Resource.error("merge error"))
            })
    }

    @SuppressLint("CheckResult")
    fun scoring(picture: File, liveData: MutableLiveData<Resource<Face>>) {
        Single.just(picture)
            .subscribeOn(Schedulers.io())
            .flatMap {
                val compressedFile = Luban.with(CoolFaceApplication.mApplicationContext)
                    .load(it)
                    .get()
                facePPService.detect("[upload]${compressedFile[0].absolutePath}", FacePPAttrUtil.Builder().default())
            }
            .subscribe({
                if (it.faces.isEmpty()) {
                    liveData.postValue(Resource.error("no face detected"))
                } else if (it.faces.size > 1) {
                    liveData.postValue(Resource.error("more than one face"))
                } else {
                    liveData.postValue(Resource.success(it.faces[0]))
                }
            }, {
                liveData.postValue(Resource.error(it.message ?: ""))
            })
    }

    fun uploadUserAvatar(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        imageFile?.absolutePath?.let {
            Single.just(it)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    val compressedFile = Luban.with(CoolFaceApplication.mApplicationContext)
                        .load(it)
                        .get()
                    smmsService.upload("[upload]${compressedFile[0].absolutePath}")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onSuccess(it.data.url)
                }, {
                    onError("upload error")
                })

        }
    }

    fun searchSameStarFace(faceToken: String, liveData: MutableLiveData<Resource<SimilarFaceInfo>>) {
        searchFace(faceToken, FacePPConfig.STAR_FACE_SET_TOKEN, BmobConfig.TABLE_STAR, liveData)
    }

    fun searchSameUserFace(faceToken: String, liveData: MutableLiveData<Resource<SimilarFaceInfo>>) {
        searchFace(faceToken, FacePPConfig.USER_FACE_SET_TOKEN, BmobConfig.TABLE_USER, liveData)
    }

    @SuppressLint("CheckResult")
    private fun searchFace(
        faceToken: String,
        facesetToken: String,
        tableName: String,
        liveData: MutableLiveData<Resource<SimilarFaceInfo>>
    ) {
        liveData.value = Resource.loading()
        var similarInfo: FacePPSearchSimilarReturn? = null
        facePPService.searchSimilarFace(faceToken, facesetToken, 1)
            .subscribeOn(Schedulers.io())
            .flatMap {
                similarInfo = it
                val newFaceToken = similarInfo?.run {
                    it.results[0].face_token
                }
                bmobService.queryData(
                    tableName,
                    "{\"faceToken\":\"$newFaceToken\"}"
                )
            }
            .subscribe({ response ->
                if (JsonUtil.toBean<BmobSimilarFaceReturn>(response.source())?.let { faceReturn ->
                        if (faceReturn.results.size == 0) {
                            liveData.postValue(Resource.error("no data"))
                            return@subscribe
                        }
                        liveData.postValue(Resource.success(faceReturn.results[0].also { similar ->
                            similarInfo?.let {
                                similar.trustLevel = it.thresholds.e4.toString()
                            }
                        }))
                    } == null) {
                    liveData.postValue(null)
                }
            }, {
                liveData.postValue(Resource.error("search error"))
            })
    }

}