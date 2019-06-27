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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.base.ListOnClickListener
import com.hustunique.coolface.bean.User
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

    private val NICKNAME_CODE = 555
    private val CAMERA_CODE = 666
    private val GALLERY_CODE = 777
    private val CROP_CODE = 888

    private var scoreWillShow = true

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
        val headerView = nav_main.getHeaderView(0)
        val avatarView = headerView.findViewById<ImageView>(R.id.iv_main_avatar)
        val nicknameView = headerView.findViewById<TextView>(R.id.tv_main_nickname)
        mViewModel.user.observe(this, Observer {
            initDrawer()
            nicknameView.text = if (BmobUser.isLogin()) it.nickname else "未登录"
            if (BmobUser.isLogin()) {
                Glide.with(this)
                    .load(it.avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarView)
            } else {
                avatarView.setImageResource(R.mipmap.logo)
            }
        })
        initDrawer()

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
        mViewModel.user.value = BmobUser.getCurrentUser(User::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    if (scoreWillShow)
                        BaseShowCard.start(this, ShowScoreFragment())
                    else {
                        mViewModel.getPictureFile()?.let {
                            val imgUri = FileProvider.getUriForFile(this, FileUtil.FILE_PROVIDER_AUTHORITY, it)
                            crop(imgUri)
                        }
                    }
                }
                GALLERY_CODE -> {
                    data?.data?.let {
                        crop(it)
                    }
                }
                CROP_CODE -> {
                    if (scoreWillShow) {
                        BaseShowCard.start(this, ShowScoreFragment())
                    } else {
                        mViewModel.upLoadAvatar {
                            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                        }
                        scoreWillShow = true
                    }
                }
                NICKNAME_CODE -> {
                    nav_main.getHeaderView(0)
                        .findViewById<TextView>(R.id.tv_main_nickname)
                        .text = data?.getStringExtra("nickname")
                }
                else -> {
                }
            }
        }
    }

    private fun startCamera() {
        val file = mViewModel.getNewPictureFile()
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

    @SuppressLint("InflateParams")
    private fun initDrawer() {
        val headerView = nav_main.getHeaderView(0)
        val nicknameView = headerView.findViewById<TextView>(R.id.tv_main_nickname)
        nicknameView.isEnabled = BmobUser.isLogin()
        nicknameView.setOnClickListener {
            BaseShowCard.start(this, NicknameCardFragment(), null, null, NICKNAME_CODE)
        }
        val avatarView = headerView.findViewById<ImageView>(R.id.iv_main_avatar)
        avatarView.isEnabled = BmobUser.isLogin()
        avatarView.setOnClickListener {
            val bottomDialog = BottomSheetDialog(this)
            val bottomDialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            bottomDialogView.findViewById<Button>(R.id.bottom_sheet_camera).setOnClickListener {
                scoreWillShow = false
                startCamera()
                bottomDialog.dismiss()
            }
            bottomDialogView.findViewById<Button>(R.id.bottom_sheet_gallery).setOnClickListener {
                scoreWillShow = false
                startGallery()
                bottomDialog.dismiss()
            }
            bottomDialog.setContentView(bottomDialogView)
            bottomDialog.show()
        }
        initNavigationMenu()
    }

    private fun initNavigationMenu() {
        if (BmobUser.isLogin()) {
            nav_main.menu.clear()
            nav_main.inflateMenu(R.menu.nav_menu_main_login)
            nav_main.setNavigationItemSelectedListener {
                when (it.itemId) {
                    // TODO: 导航
                    R.id.nav_all -> {
                        mViewModel.getPosts()
                        true
                    }
                    R.id.nav_mine -> {
                        mViewModel.updateMyPosts()
                        true
                    }
                    R.id.nav_star -> {
                        true
                    }
                    R.id.nav_logout -> {
                        BmobUser.logOut()
                        Toast.makeText(applicationContext, "已退出", Toast.LENGTH_SHORT).show()
                        mViewModel.user.value = BmobUser.getCurrentUser(User::class.java)
                        false
                    }
                    else -> false
                }
            }
        } else {
            nav_main.menu.clear()
            nav_main.inflateMenu(R.menu.nav_menu_main)
            nav_main.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_login -> {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        false
                    }
                    else -> false
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_CODE)
    }

    private fun crop(uri: Uri) {
        val file = mViewModel.getNewPictureFile()
        file?.let {
            val saveFile = Uri.fromFile(it)
            val intent = Intent("com.android.camera.action.CROP")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION +
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            intent.setDataAndType(uri, "image/*")
            if (scoreWillShow) {
                intent.putExtra("aspectX", 768)
                intent.putExtra("aspectY", 1024)
            } else {
                intent.putExtra("aspectX", 1)
                intent.putExtra("aspectY", 1)
            }
            intent.putExtra("scale", true)
            intent.putExtra("return-data", false)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFile)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.putExtra("noFaceDetection", true)
            startActivityForResult(intent, CROP_CODE)
        }
    }

}
