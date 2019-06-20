package com.hustunique.coolface.model.local

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.remote.FacePPService
import com.hustunique.coolface.model.remote.RetrofitService
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
                val beautifiedPicture = FileUtil.createImageFile(context)
                beautifiedPicture?.let {
                    val sink = beautifiedPicture.sink().buffer()
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
}