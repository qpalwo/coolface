package com.hustunique.coolface.model.local

import android.content.Context
import com.hustunique.coolface.util.FileUtil
import java.io.File

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
class PictureRepo private constructor(context: Context) {
    companion object {
        private lateinit var Instance: PictureRepo
        fun getInstance(context: Context): PictureRepo {
            if (!::Instance.isInitialized) {
                Instance = PictureRepo(context)
            }
            return Instance
        }
    }

    var imageFile: File? = FileUtil.createImageFile(context)

    fun getFile() = imageFile
}