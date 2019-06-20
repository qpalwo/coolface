package com.hustunique.coolface.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.Fade
import android.view.Gravity.START
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
import com.hustunique.coolface.showcard.ShowActivity
import com.hustunique.coolface.showscore.ShowScoreActivity
import com.hustunique.coolface.util.FileUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(R.layout.activity_main, MainViewModel::class.java) {

    val CAMERA_CODE = 666
    private lateinit var mViewModel: MainViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as MainViewModel
        Bmob.initialize(this, "12087a50147473005dcbe686a04bf4f1")
        mViewModel.init()
    }

    override fun initView() {
        super.initView()
        main_list.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        main_list.adapter = MainAdapter()
        setupEnterExitAni()
    }

    private fun setupEnterExitAni() {

    }


    @SuppressLint("WrongConstant")
    override fun initContact() {
        super.initContact()
        mViewModel.posts.observe(this, Observer {
            (main_list.adapter as MainAdapter).data = it
            (main_list.adapter as MainAdapter).notifyDataSetChanged()
        })
        mainactivity_fb.setOnClickListener {
            startCamera()
        }
        main_me.setOnClickListener {
            main_drawerlayout.openDrawer(START)
        }
        (main_list.adapter as MainAdapter).clickListener = object : ListOnClickListener {
            override fun onClick(position: Int, v: View) {
                val intent = Intent(this@MainActivity, ShowActivity::class.java)
                intent.putExtra(getString(R.string.post), mViewModel.posts.value?.get(position))
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        (main_list.adapter as MainAdapter).getSharedWeight(position),
                        getString(R.string.post_shared)
                    )
                startActivity(intent, options.toBundle())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    val intent = Intent(this, ShowScoreActivity::class.java)
                    startActivity(intent)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                val imgUri = FileProvider.getUriForFile(this, FileUtil.FILE_PROVIDER_AUTHORITY, it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(it))
            }
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, CAMERA_CODE)
            }
        }
    }
}
