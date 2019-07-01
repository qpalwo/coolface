package com.hustunique.coolface.fusion

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.*
import android.widget.Button
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hustunique.coolface.R
import com.hustunique.coolface.model.repo.PictureRepo
import com.hustunique.coolface.picture.PictureActivity
import com.hustunique.coolface.show.BaseShowFragment
import com.hustunique.coolface.util.DialogUtil
import com.hustunique.coolface.util.FileUtil
import com.hustunique.coolface.util.LiveDataUtil
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
    private var resultShowAnimator: AnimatorSet? = null

    private var fusionImageStartPoint: Point? = null
    private var templateImageStartPoint: Point? = null

    private var mergeResultImage: String? = null

    private lateinit var mViewModel: FusionViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as FusionViewModel
    }


    override fun initContact(context: Context?) {
        super.initContact(context)
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
            if (fusionImageFile != null && templateImageFile != null) {
                fusion_confirm.isEnabled = false
                fusion {
                    mViewModel.mergeFace(templateImageFile!!, fusionImageFile!!)
                }
            } else {
                DialogUtil.showTipDialog(context!!, "必须添加图片哦", "确定", {
                    it.doDismiss()
                })
            }
        }

        fusion_result_image.setOnClickListener {
            mergeResultImage?.let {
                startPictureActivity(fusion_result_image, it)
            }
        }

        mViewModel.mergeResult.observe(this, Observer {
            LiveDataUtil.useData(it, {
                showResult(it!!)
                animationSet.cancel()
                animationSet = AnimatorSet()
                fusion_confirm.isEnabled = true
            }, error = { s, d ->
                animationSet.cancel()
                animationSet = AnimatorSet()
                fusion_confirm.isEnabled = false
                DialogUtil.showTipDialog(context!!, "融合出错了。嘤嘤嘤", "确定", {
                    it.doDismiss()
                })
                toast(s!!)
            })
        })
    }

    /**
     * 传入的是开始融合的表达式
     *
     */
    private fun fusion(startMerge: () -> Unit) {
        val targetX = fusion_result_image.x + 36
        val targetY = fusion_result_image.y + 64
        fusion_template_tip.visibility = INVISIBLE
        fusion_fusion_tip.visibility = INVISIBLE
        if (fusionImageStartPoint == null) {
            fusionImageStartPoint = Point(fusion_fusion_image.x.toInt(), fusion_fusion_image.y.toInt())
        }
        if (templateImageStartPoint == null) {
            templateImageStartPoint = Point(fusion_template_image.x.toInt(), fusion_template_image.y.toInt())
        }
        val moveFusionXAnimator =
            ValueAnimator.ofFloat(fusion_fusion_image.x, targetX).apply {
                duration = 1500
                addUpdateListener {
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

        val moveAnimators = AnimatorSet().apply {
            playTogether(
                moveFusionXAnimator,
                moveFusionYAnimator,
                moveTemplateXAnimator,
                moveTemplateYAnimator,
                resultDispearAnimator
            )
        }


        val rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1500
            repeatCount = INFINITE
            addUpdateListener {
                if (it.currentPlayTime == 0L) {
                    startMerge.invoke()
                }
                fusion_template_image.rotation = it.animatedValue as Float
                fusion_fusion_image.rotation = -(it.animatedValue as Float)
            }
        }

        val sameTimeAlphaAnimator = ValueAnimator.ofFloat(1f, 0.3f, 1f).apply {
            duration = 1500
            repeatCount = INFINITE
            addUpdateListener {
                fusion_template_image.alpha = it.animatedValue as Float
                fusion_fusion_image.alpha = 1.3f - it.animatedValue as Float
            }
        }

        val mergingAnimators = AnimatorSet().apply {
            playTogether(rotationAnimator, sameTimeAlphaAnimator)
        }

        animationSet.apply {
            playSequentially(
                moveAnimators,
                mergingAnimators
            )
            start()
        }

        if (resultShowAnimator == null) {
            resultShowAnimator = AnimatorSet()

            val fusionImageDisappearAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = 1500
                addUpdateListener {
                    fusion_template_image.alpha = it.animatedValue as Float
                    fusion_fusion_image.alpha = it.animatedValue as Float
                }
            }

            val resultAppearAnimator = ValueAnimator.ofFloat(fusion_result_image.alpha, 1f).apply {
                duration = 1500
                addUpdateListener {
                    fusion_result_image.alpha = it.animatedValue as Float
                }
            }

            val resultShowAndFusionMiss = AnimatorSet().apply {
                playTogether(fusionImageDisappearAnimator, resultAppearAnimator)
            }

            val fusionImageAppearAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1500
                addUpdateListener {
                    fusion_template_image.alpha = it.animatedValue as Float
                    fusion_fusion_image.alpha = it.animatedValue as Float
                }
            }

            (resultShowAnimator as AnimatorSet).apply {
                playSequentially(resultShowAndFusionMiss, fusionImageAppearAnimator)
            }
        }
    }

    private fun showResult(resultUrl: String) {
        animationSet.pause()
        
        fusion_template_image.apply {
            rotation = 0f
            x = templateImageStartPoint?.x!!.toFloat()
            y = templateImageStartPoint?.y!!.toFloat()
            fusion_template_tip.visibility = VISIBLE
        }
        fusion_fusion_image.apply {
            rotation = 0f
            x = fusionImageStartPoint!!.x.toFloat()
            y = fusionImageStartPoint!!.y.toFloat()
            fusion_fusion_tip.visibility = VISIBLE
        }
        resultShowAnimator?.start()
        mergeResultImage = resultUrl
        Glide.with(this).load(resultUrl).into(fusion_result_image)
    }

    private fun startPictureActivity(shareView: View, url: String) {
        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                getOuterActivity(),
                shareView,
                getString(R.string.image_shared)
            )
        startActivity(PictureActivity::class.java, Bundle().apply {
            putString(PictureActivity.PICTURE, url)
        }, options.toBundle())
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
                    val file = PictureRepo.getInstance().getFile()
                    if (clickPosition == TEMPLATE_CODE) {
                        templateImageFile = file!!
                    } else if (clickPosition == FUSION_CODE) {
                        fusionImageFile = file!!
                    }
                    Glide.with(this).load(file).into(
                        when (clickPosition) {
                            TEMPLATE_CODE -> {
                                templateImageCroppedUri = file!!.absolutePath
                                fusion_template_tip.children.forEach {
                                    it.visibility = GONE
                                }
                                fusion_template_image
                            }
                            FUSION_CODE -> {
                                fusionImageCroppedUri = file!!.absolutePath
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