package com.hustunique.coolface.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.hustunique.coolface.base.CoolFaceApplication
import okio.buffer
import okio.sink
import okio.source
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
                CoolFaceApplication.mApplicationContext.getContentResolver(),
                inPut.absolutePath,
                inPut.name,
                null
            )
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = FileProvider.getUriForFile(CoolFaceApplication.mApplicationContext, FILE_PROVIDER_AUTHORITY, inPut)
            intent.data = uri
            CoolFaceApplication.mApplicationContext.sendBroadcast(intent)
            onSuccess?.run {
                this("success")
            }
        } catch (e: Exception) {
            onError?.run {
                this(e.message.toString())
            }
        }

    }
}