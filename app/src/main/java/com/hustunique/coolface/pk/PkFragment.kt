package com.hustunique.coolface.pk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.View.*
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hustunique.coolface.R
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.show.BaseShowFragment
import com.hustunique.coolface.util.*
import com.kongzue.dialog.v2.CustomDialog
import kotlinx.android.synthetic.main.fra_pk.*

class PkFragment : BaseShowFragment(R.layout.fra_pk, PkViewModel::class.java) {
    private val CAMERA_CODE = 111
    private val GALLERY_CODE = 222
    private val CROP_CODE = 333

    private lateinit var post: Post
    private lateinit var mViewModel: PkViewModel

    private var mineImage: Uri? = null

    private var dialog: CustomDialog? = null

    override fun init() {
        super.init()
        mViewModel = viewModel as PkViewModel
    }

    override fun initData() {
        super.initData()
        post = arguments!!.getSerializable(getString(R.string.post)) as Post
    }

    override fun initView(view: View) {
        super.initView(view)

        TextUtil.setDefaultTypeface(
            pk_mine_result,
            pk_opponent_score,
            pk_opponent_username,
            pk_mine_username,
            pk_mine_score
        )

        Glide.with(this).load(post.faceBean.faceUrl).into(pk_opponent_image)

        pk_opponent_username.text = post.username
        pk_opponent_score.text = post.face!!.attributes.beauty.let {
            if (it.female_score > it.male_score)
                it.female_score.toInt().toString()
            else
                it.male_score.toInt().toString()
        }

        pk_mine_username.text = BmobUser.getCurrentUser(User::class.java).nickname
    }

    override fun initContact(context: Context?) {
        super.initContact(context)
        mViewModel.scoringData.observe(this, Observer {
            LiveDataUtil.useData(it, {
                pk_post.backgroundTintList = context!!.getColorStateList(R.color.colorAccent)
                pk_mine_score.text = it?.attributes!!.beauty.let {
                    if (it.female_score > it.male_score) {
                        it.female_score.toInt().toString()
                    } else {
                        it.male_score.toInt().toString()
                    }
                }
                showMineResult()
                showSuccessAnimation()
                dialog?.doDismiss()
                pk_post.isEnabled = true
                pk_post.backgroundTintList = context!!.getColorStateList(R.color.colorAccent)
            }, error = { s, d ->
                DialogUtil.showTipDialog(context!!, "评分失败: $s", "确定", {
                    it.doDismiss()
                    getOuterActivity().finish()
                })
            })
        })

        pk_mine_image.setOnClickListener {
            val bottomDialog = BottomSheetDialog(context!!)
            val bottomDialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            bottomDialogView.findViewById<Button>(R.id.bottom_sheet_camera).setOnClickListener {
                startCamera()
                bottomDialog.dismiss()
            }
            bottomDialogView.findViewById<Button>(R.id.bottom_sheet_gallery).setOnClickListener {
                startGallery()
                bottomDialog.dismiss()
            }
            bottomDialog.setContentView(bottomDialogView)
            bottomDialog.show()
        }

        pk_post.setOnClickListener {
            getOuterActivity().setResult(Activity.RESULT_OK)
            getOuterActivity().supportFinishAfterTransition()
        }
    }

    private fun showMineResult() {
        pk_mine_result.visibility = VISIBLE
        if (pk_opponent_score.text.toString().toDouble() > pk_mine_score.text.toString().toDouble()) {
            pk_mine_result.text = "You Lost"
            pk_mine_result.setTextColor(context!!.getColorStateList(android.R.color.darker_gray))
        } else {
            pk_mine_result.text = "You Win"
            pk_mine_result.setTextColor(context!!.getColorStateList(R.color.colorAccent2))
        }
    }

    private fun showSuccessAnimation() {
        val animation = if (pk_opponent_score.text.toString().toDouble() > pk_mine_score.text.toString().toDouble()) {
            pk_opponent_ani
        } else pk_mine_ani

        animation.apply {
            AnimationUtil.lottiePlayOnce(this)
            animation.visibility = VISIBLE
            animation.playAnimation()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    mViewModel.getPictureFile()?.let {
                        mineImage = FileProvider.getUriForFile(context!!, FileUtil.FILE_PROVIDER_AUTHORITY, it)
                        crop(mineImage!!)
                    }
                }
                GALLERY_CODE -> {
                    data?.data?.let {
                        mineImage = it
                        crop(it)
                    }
                }
                CROP_CODE -> {
                    setMineImage()
                }
            }
        }
    }

    private fun setMineImage() {
        pk_mine_result.visibility = INVISIBLE
        dialog = DialogUtil.showProgressDialog(context!!, "评分中")
        mViewModel.scoring()
        pk_tip.visibility = GONE
        Glide.with(this).load(mineImage).into(pk_mine_image)
    }

    private fun startCamera() {
        val file = mViewModel.getNewPictureFile()
        file?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION +
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val imgUri = FileProvider.getUriForFile(context!!, FileUtil.FILE_PROVIDER_AUTHORITY, it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(context!!.packageManager)?.let {
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
        val file = mViewModel.getNewPictureFile()
        file?.let {
            val saveFile = Uri.fromFile(it)
            val intent = Intent("com.android.camera.action.CROP")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION +
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("aspectX", 18)
            intent.putExtra("aspectY", 26)
            intent.putExtra("scale", true)
            intent.putExtra("return-data", false)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFile)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.putExtra("noFaceDetection", true)
            startActivityForResult(intent, CROP_CODE)
        }
    }
}