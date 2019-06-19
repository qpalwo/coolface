package com.hustunique.coolface.mtest

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import kotlinx.android.synthetic.main.activity_test.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TestActivity : BaseActivity(R.layout.activity_test) {
    val AUTHORITY = "com.hustunique.coolface.fileProvider"
    val REQUEST_CODE_CAPTURE_RAW = 6 //startActivityForResult时的请求码
    var imageFile: File? = null     //拍照后保存的照片
    var imgUri: Uri? = null         //拍照后保存的照片的uri

    override fun initView() {
        super.initView()
        take_photo.setOnClickListener {
            gotoCaptureRaw()
        }
    }

    private fun gotoCaptureRaw() {
        imageFile = createImageFile()
        imageFile?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //如果是7.0以上，使用FileProvider，否则会报错
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                imgUri = FileProvider.getUriForFile(this, AUTHORITY, it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri) //设置拍照后图片保存的位置
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(it)) //设置拍照后图片保存的位置
            }
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()) //设置图片保存的格式
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_RAW) //调起系统相机
            }
        }
    }

    //生成一个文件
    fun createImageFile(isCrop: Boolean = false): File? {
        return try {
            val rootFile = File(getExternalFilesDir(null), "picture")
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
}
