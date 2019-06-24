package com.hustunique.coolface.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.bmob.v3.Bmob
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.base.ListOnClickListener
import com.hustunique.coolface.login.SignupActivity
import com.hustunique.coolface.model.remote.config.BmobConfig
import com.hustunique.coolface.show.BaseShowCard
import com.hustunique.coolface.showcard.ShowCardFragment
import com.hustunique.coolface.showscore.ShowScoreActivity
import com.hustunique.coolface.showscore.ShowScoreFragment
import com.hustunique.coolface.util.FileUtil
import com.hustunique.coolface.util.LivaDataUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(R.layout.activity_main, MainViewModel::class.java) {

    private val CAMERA_CODE = 666
    private val GALLERY_CODE = 777
    private val CROP_CODE = 888

    private lateinit var mViewModel: MainViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as MainViewModel
        Bmob.initialize(this, BmobConfig.APPLICATION_ID)
        mViewModel.init(applicationContext)
    }

    override fun initView() {
        super.initView()
        main_list.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        main_list.adapter = MainAdapter()
    }


    @SuppressLint("WrongConstant")
    override fun initContact() {
        super.initContact()
        mViewModel.postsData.observe(this, Observer {
            LivaDataUtil.useData(it, {
                (main_list.adapter as MainAdapter).data = it
                (main_list.adapter as MainAdapter).notifyDataSetChanged()

            })
        })
        main_activity_camera_fb.setOnClickListener {
            BaseShowCard.start(this, ShowScoreFragment())
//            startCamera()
        }
        main_activity_gallery_fb.setOnClickListener {
            startGallery()
        }
        main_me.setOnClickListener {
            // TODO 如果已经登录 弹出抽屉 如果没有登录 跳转登录页面
            // main_drawerlayout.openDrawer(START)
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        (main_list.adapter as MainAdapter).clickListener = object : ListOnClickListener {
            override fun onClick(position: Int, v: View) {
                LivaDataUtil.useData(mViewModel.postsData, {
                    val options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MainActivity,
                            (main_list.adapter as MainAdapter).getSharedWeight(position),
                            getString(R.string.post_shared)
                        )
                    BaseShowCard.start(this@MainActivity, ShowCardFragment(), Bundle().apply {
                        putSerializable(
                            getString(R.string.post),
                            it?.get(position)
                        )
                    }, options.toBundle())
                })

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE, CROP_CODE -> {
                    val intent = Intent(this, ShowScoreActivity::class.java)
                    startActivity(intent)
                }
                GALLERY_CODE -> {
                    data?.data?.let {
                        crop(it)
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun startCamera() {
        val file = mViewModel.getPictureFile(applicationContext)
        file?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION +
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val imgUri = FileProvider.getUriForFile(this, FileUtil.FILE_PROVIDER_AUTHORITY, it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, CAMERA_CODE)
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_CODE)
    }

    private fun crop(uri: Uri) {
        val file = mViewModel.getPictureFile(applicationContext)
        file?.let {
            val saveFile = Uri.fromFile(it)
            val intent = Intent("com.android.camera.action.CROP")
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("aspectX", 768)
            intent.putExtra("aspectY", 1024)
            intent.putExtra("scale", true)
            intent.putExtra("return-data", false)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFile)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.putExtra("noFaceDetection", true)
            startActivityForResult(intent, CROP_CODE)
        }
    }
}
