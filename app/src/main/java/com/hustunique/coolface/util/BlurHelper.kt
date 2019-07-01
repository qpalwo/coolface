package com.hustunique.coolface.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewTreeObserver
import com.hustunique.coolface.base.BaseActivity
//import com.zhouwei.blurlibrary.EasyBlur


/**
 * @author  : Xiao Yuxuan
 * @date    : 7/1/19
 */
class BlurHelper {

    companion object {
        val Instance = BlurHelper()
    }

    private var backGround: Bitmap? = null

    private var backView: View? = null

    private var onSuccess: (() -> Unit?)? = null

    private val listener = ViewTreeObserver.OnGlobalLayoutListener {
        backView?.let {
            remove()
            backGround = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(backGround)
            it.layout(0, 0, it.layoutParams.width, it.layoutParams.height)
            it.draw(canvas)
            onSuccess?.run {
                this()
            }
        }
    }

    fun initBlur(activity: BaseActivity, onSuccess: () -> Unit?) {
        this.onSuccess = onSuccess
        backView = activity.window.decorView
//        backView?.viewTreeObserver?.addOnDrawListener()
        backView?.viewTreeObserver?.addOnGlobalLayoutListener(listener)
//        backView?.let {
//            it.layout(0, 0, it.layoutParams.width, it.layoutParams.height)
//        }
    }

    fun blurBackGround(activity: BaseActivity) {
        backGround?.let {
            activity.window.decorView.background = BitmapDrawable(
//                EasyBlur.with(activity)
//                    .bitmap(backGround)
//                    .radius(10)
//                    .scale(4)
//                    .policy(EasyBlur.BlurPolicy.FAST_BLUR)
//                    .blur()
            )
        }
    }

    private fun remove() {
        backView?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
    }
}