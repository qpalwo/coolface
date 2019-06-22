package com.hustunique.coolface.model.repo

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.remote.RetrofitService
import com.hustunique.coolface.model.remote.bean.Face
import com.hustunique.coolface.model.remote.config.BeautifyLevel
import com.hustunique.coolface.model.remote.service.FacePPService
import com.hustunique.coolface.util.FacePPAttrUtil
import com.hustunique.coolface.util.FileUtil
import io.reactivex.Single
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
class PictureRepo private constructor(val context: Context) {
    companion object {
        private lateinit var Instance: PictureRepo
        fun getInstance(context: Context): PictureRepo {
            if (!::Instance.isInitialized) {
                Instance = PictureRepo(context)
            }
            return Instance
        }
    }

    private var imageFile: File? = FileUtil.createImageFile(context)

    var beautifiedPicture: File? = null

    private val facePPService = RetrofitService.Instance.facePPRetrofit.create(FacePPService::class.java)

    fun getFile() = imageFile

    fun getNewFile(): File? {
        imageFile = FileUtil.createImageFile(context)
        return imageFile
    }

    @SuppressLint("CheckResult")
    fun beautify(picture: String, pictureData: MutableLiveData<Resource<String>>) {
        Single.just(picture)
            .subscribeOn(Schedulers.io())
            .flatMap {
                val compressedFile = Luban.with(context)
                    .load(it)
                    .get()
                facePPService.beautify(
                    "[upload]${compressedFile[0].absolutePath}",
                    BeautifyLevel.whitening,
                    BeautifyLevel.smoothing
                )
            }
            .map { data ->
                beautifiedPicture = FileUtil.createImageFile(context)
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
    fun scoring(picture: File, liveData: MutableLiveData<Resource<Face>>) {
        Single.just(picture)
            .subscribeOn(Schedulers.io())
            .flatMap {
                val compressedFile = Luban.with(context)
                    .load(it)
                    .get()
                facePPService.detect("[upload]${compressedFile[0].absolutePath}", FacePPAttrUtil.Builder().default())
            }
            .subscribe({
                if (it.faces.size == 0) {
                    liveData.postValue(Resource.error("no face detected"))
                } else if (it.faces.size > 1) {
                    liveData.postValue(Resource.error("more than one face"))
                }
                liveData.postValue(Resource.success(it.faces[0]))
            }, {
                liveData.postValue(Resource.error(it.message ?: ""))
            })
    }

}