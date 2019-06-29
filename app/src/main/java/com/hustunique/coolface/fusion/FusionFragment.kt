package com.hustunique.coolface.fusion

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hustunique.coolface.R
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.show.BaseShowFragment
import com.hustunique.coolface.util.FileUtil
import kotlinx.android.synthetic.main.fra_fusion.*
import java.io.File

class FusionFragment : BaseShowFragment(R.layout.fra_fusion, FusionViewModel::class.java) {
    private val TEMPLATE_CODE = 123
    private val FUSION_CODE = 456


    private val GALLERY_CODE = 111
    private val CAMERA_CODE = 222
    private val CROP_CODE = 333

    private var clickPosition = 0

    private var templateImageFile: File? = null
    private var fusionImageFile: File? = null

    private var templateImageCroppedUri = ""
    private var fusionImageCroppedUri = ""

    private var animationSet: AnimatorSet = AnimatorSet()

    private lateinit var mViewModel: FusionViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as FusionViewModel
    }

    override fun initView(view: View) {
        super.initView(view)
        fusion_template_tip.setOnClickListener {
            val bottomDialog = BottomSheetDialog(context!!)
            val bottomDialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            bottomDialogView.findViewById<Button>(R.id.bottom_sheet_camera).setOnClickListener {
                clickPosition = TEMPLATE_CODE
                startCamera()
                bottomDialog.dismiss()
            }
            bottomDialogView.findViewById<Button>(R.id.bottom_sheet_gallery).setOnClickListener {
                clickPosition = TEMPLATE_CODE
                startGallery()
                bottomDialog.dismiss()
            }
            bottomDialog.setContentView(bottomDialogView)
            bottomDialog.show()
        }

        fusion_fusion_tip.setOnClickListener {
            val bottomDialog = BottomSheetDialog(context!!)
            val bottomDialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            bottomDialogView.findViewById<Button>(R.id.bottom_sheet_camera).setOnClickListener {
                clickPosition = FUSION_CODE
                startCamera()
                bottomDialog.dismiss()
            }
            bottomDialogView.findViewById<Button>(R.id.bottom_sheet_gallery).setOnClickListener {
                clickPosition = FUSION_CODE
                startGallery()
                bottomDialog.dismiss()
            }
            bottomDialog.setContentView(bottomDialogView)
            bottomDialog.show()
        }

        fusion_confirm.setOnClickListener {
            fusion()
        }
    }

    private fun fusion() {
        val targetX = fusion_result_image.x + 36
        val targetY = fusion_result_image.y + 64
        fusion_template_tip.visibility = INVISIBLE
        fusion_fusion_tip.visibility = INVISIBLE
        val moveFusionXAnimator =
            ValueAnimator.ofFloat(fusion_fusion_image.x, targetX).apply {
                duration = 1500
                addUpdateListener {
                    if (it.currentPlayTime == 0L) {
                        // TODO: 开始融合
                    }
                    fusion_fusion_image.x = it.animatedValue as Float
                }
            }
        val moveFusionYAnimator =
            ValueAnimator.ofFloat(fusion_fusion_image.y, targetY).apply {
                duration = 1500
                addUpdateListener {
                    fusion_fusion_image.y = it.animatedValue as Float
                }
            }
        val moveTemplateXAnimator =
            ValueAnimator.ofFloat(fusion_template_image.x, targetX).apply {
                duration = 1500
                addUpdateListener {
                    fusion_template_image.x = it.animatedValue as Float
                }
            }
        val moveTemplateYAnimator =
            ValueAnimator.ofFloat(fusion_template_image.y, targetY).apply {
                duration = 1500
                addUpdateListener {
                    fusion_template_image.y = it.animatedValue as Float
                }
            }

        val resultDispearAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 1500
            addUpdateListener {
                fusion_result_image.alpha = it.animatedValue as Float
            }
        }

        val rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1500
            repeatCount = INFINITE
            addUpdateListener {
                fusion_template_image.rotation = it.animatedValue as Float
                fusion_fusion_image.rotation = -(it.animatedValue as Float)
            }
        }

        val alphaAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 1500
            addUpdateListener {
                fusion_template_image.alpha = it.animatedValue as Float
                fusion_fusion_image.alpha = it.animatedValue as Float
            }
        }

        val moveAnimators = AnimatorSet().apply {
            playTogether(
                moveFusionXAnimator,
                moveFusionYAnimator,
                moveTemplateXAnimator,
                moveTemplateYAnimator,
                resultDispearAnimator
            )
        }

        animationSet.apply {
            playSequentially(
                moveAnimators,
                rotationAnimator
            )
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animationSet.pause()
    }

    private fun startCamera() {
        val requestCode = CAMERA_CODE
        val file = mViewModel.getNewPictureFile()
        if (clickPosition == TEMPLATE_CODE) {
            templateImageFile = file!!
        } else if (clickPosition == FUSION_CODE) {
            fusionImageFile = file!!
        }
        file.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION +
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val imgUri = FileProvider.getUriForFile(context!!, FileUtil.FILE_PROVIDER_AUTHORITY, it!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(context!!.packageManager)?.let {
                startActivityForResult(intent, requestCode)
            }
        }
    }

    private fun startGallery() {
        val requestCode = GALLERY_CODE
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    private fun crop(uri: Uri) {
        val file = mViewModel.getNewPictureFile()
        file?.let {
            val saveFile = Uri.fromFile(it)
            val intent = Intent("com.android.camera.action.CROP")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION +
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("aspectX", 9)
            intent.putExtra("aspectY", 16)
            intent.putExtra("scale", true)
            intent.putExtra("return-data", false)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFile)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.putExtra("noFaceDetection", true)
            startActivityForResult(intent, CROP_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    crop(
                        FileProvider.getUriForFile(
                            context!!, FileUtil.FILE_PROVIDER_AUTHORITY, when (clickPosition) {
                                TEMPLATE_CODE -> {
                                    templateImageFile!!
                                }
                                FUSION_CODE -> {
                                    fusionImageFile!!
                                }
                                else -> templateImageFile!!
                            }
                        )
                    )
                }
                GALLERY_CODE -> {
                    data?.data?.let {
                        crop(it)
                    }
                }
                CROP_CODE -> {
                    Glide.with(this).load(PictureRepo.getInstance().getFile()).into(
                        when (clickPosition) {
                            TEMPLATE_CODE -> {
                                templateImageCroppedUri = PictureRepo.getInstance().getFile()!!.absolutePath
                                fusion_template_tip.children.forEach {
                                    it.visibility = GONE
                                }
                                fusion_template_image
                            }
                            FUSION_CODE -> {
                                fusionImageCroppedUri = PictureRepo.getInstance().getFile()!!.absolutePath
                                fusion_fusion_tip.children.forEach {
                                    it.visibility = GONE
                                }
                                fusion_fusion_image
                            }
                            else -> {
                                fusion_result_image
                            }
                        }
                    )
                }
            }
        }
    }


}