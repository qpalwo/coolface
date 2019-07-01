package com.hustunique.coolface.util

import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import com.hustunique.coolface.base.CoolFaceApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author  : Xiao Yuxuan
 * @date    : 6/19/19
 */
object FileUtil {

    const val PICTURE_PATH = "picture"
    const val FILE_PROVIDER_AUTHORITY = "com.hustunique.coolface.fileProvider"

    fun createImageFile(rootFile: File, isCrop: Boolean = false): File? {
        return try {
            if (!rootFile.exists())
                rootFile.mkdirs()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val fileName = if (isCrop) "IMG_${timeStamp}_CROP.jpg" else "IMG_$timeStamp.jpg"
            File(rootFile.absolutePath + File.separator + fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createImageFile(context: Context, isCrop: Boolean = false): File? {
        return try {
            val rootFile = context.getExternalFilesDir(PICTURE_PATH)
            rootFile?.let {
                if (!rootFile.exists())
                    rootFile.mkdirs()
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val fileName = if (isCrop) "IMG_${timeStamp}_CROP.jpg" else "IMG_$timeStamp.jpg"
                File(rootFile.absolutePath + File.separator + fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun save2Gallery(path: String, onSuccess: ((String) -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        try {
            val inPut = File(path)
            MediaStore.Images.Media.insertImage(
                CoolFaceApplication.mApplicationContext.contentResolver,
                inPut.absolutePath,
                inPut.name,
                null
            )
            MediaScannerConnection
                .scanFile(
                    CoolFaceApplication.mApplicationContext,
                    arrayOf(inPut.absolutePath),
                    arrayOf("image/jpeg")
                ) { p, uri ->
                    onSuccess?.run {
                        this(p)
                    }
                }
        } catch (e: Exception) {
            onError?.run {
                this(e.message.toString())
            }
        }

    }
}