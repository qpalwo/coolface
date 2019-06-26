package com.hustunique.coolface.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity.START
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.bmob.v3.BmobUser
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.base.ListOnClickListener
import com.hustunique.coolface.login.LoginActivity
import com.hustunique.coolface.main.navigation.NicknameCardFragment
import com.hustunique.coolface.show.BaseShowCard
import com.hustunique.coolface.showcard.ShowCardFragment
import com.hustunique.coolface.showscore.ShowScoreFragment
import com.hustunique.coolface.util.FileUtil
import com.hustunique.coolface.util.LiveDataUtil
import com.hustunique.coolface.util.TextUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(R.layout.activity_main, MainViewModel::class.java) {

    private val CAMERA_CODE = 666
    private val GALLERY_CODE = 777
    private val CROP_CODE = 888

    // 点击查看详情的位置
    private var clickPosition: Int = -1

    private lateinit var mViewModel: MainViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as MainViewModel
        mViewModel.init()
        initDrawer()
    }

    override fun initView() {
        super.initView()

        main_list.adapter = MainAdapter(mViewModel)
        main_list.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

        TextUtil.setDefaultTypeface(main_title)
    }


    @SuppressLint("WrongConstant")
    override fun initContact() {
        super.initContact()
        mViewModel.postsData.observe(this, Observer {
            LiveDataUtil.useData(it, {
                (main_list.adapter as MainAdapter).apply {
                    data = it
                    notifyDataSetChanged()
                }
            })
        })

        mViewModel.postData.observe(this, Observer {
            LiveDataUtil.useData(it, {
                (main_list.adapter as MainAdapter).apply {
                    (data as MutableList)[clickPosition] = it!!
                    notifyItemChanged(clickPosition)
                }
            })
        })

        mViewModel.user.observe(this, Observer {
            val headerView = nav_main.getHeaderView(0)
//            val avatarView = headerView.findViewById<ImageView>(R.id.iv_main_avatar)
            val nicknameView = headerView.findViewById<TextView>(R.id.tv_main_nickname)
            // TODO: 登录
//            nicknameView.text = it.nickname
        })
        main_activity_camera_fb.setOnClickListener {
            floatingActionsMenu.collapse()
            startCamera()
        }
        main_activity_gallery_fb.setOnClickListener {
            floatingActionsMenu.collapse()
            startGallery()
        }
        main_me.setOnClickListener {
            main_drawerlayout.openDrawer(START)
        }
        (main_list.adapter as MainAdapter).clickListener = object : ListOnClickListener {
            override fun onClick(position: Int, v: View) {
                LiveDataUtil.useData(mViewModel.postsData, {
                    clickPosition = position
                    val options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MainActivity,
                            (main_list.adapter as MainAdapter).getSharedWeight(position),
                            getString(R.string.image_shared)
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

    override fun onResume() {
        super.onResume()
        if (clickPosition != -1) {
            // 屏蔽局部刷新动画
            (main_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            mViewModel.updatePostAt(clickPosition)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE, CROP_CODE -> {
                    BaseShowCard.start(this, ShowScoreFragment())
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
        val file = mViewModel.getPictureFile()
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

    private fun initDrawer() {
        val headerView = nav_main.getHeaderView(0)
        val nicknameView = headerView.findViewById<TextView>(R.id.tv_main_nickname)
        nicknameView.isClickable = BmobUser.isLogin()
        nicknameView.setOnClickListener {
            val nicknameFragment = NicknameCardFragment()
            BaseShowCard.start(this, nicknameFragment)
        }
        val avatarView = headerView.findViewById<ImageView>(R.id.iv_main_avatar)
        avatarView.isClickable = BmobUser.isLogin()
        avatarView.setOnClickListener {

        }
        nav_main.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    false
                }
                R.id.nav_setting -> {
                    false
                }
                else -> false
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_CODE)
    }

    private fun crop(uri: Uri) {
        val file = mViewModel.getPictureFile()
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
