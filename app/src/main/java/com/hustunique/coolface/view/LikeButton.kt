package com.hustunique.coolface.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.widget.ImageView
import com.hustunique.coolface.R

class LikeButton(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    private var drawables: Array<Drawable>

    var onCheckedListener: OnCheckedListener? = null

    private companion object {
        val HEART = 1
        val STAR = 2
        val THUMB = 3
    }

    init {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.LikeButton)
        val mode = ta?.getInt(R.styleable.LikeButton_ltheme, -1)
        val colors = ta?.getColorStateList(R.styleable.LikeButton_accentColor)

        drawables = when (mode) {
            HEART -> arrayOf(
                context.getDrawable(R.drawable.like_heart_uncheck_24dp)!!,
                context.getDrawable(R.drawable.like_heart_checked_24dp)!!
            )
            STAR -> arrayOf(
                context.getDrawable(R.drawable.like_star_unchecked_24dp)!!,
                context.getDrawable(R.drawable.like_star_checked_24dp)!!
            )
            else -> arrayOf()
        }

        drawables.forEach { it.setTintList(colors) }

        setImageDrawable(drawables[0])

        ta?.recycle()
    }

    private var isChecked = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == ACTION_DOWN) {
            performAnimation()
            if (isChecked)
                unCheck()
            else
                check()
            return true
        }
        return super.onTouchEvent(event)

    }

    private fun performAnimation() {
        val animator = ValueAnimator.ofFloat(1.0f, 0.8f, 1.2f, 1.0f).setDuration(500)
        animator.addUpdateListener {
            this@LikeButton.scaleX = it.animatedValue as Float
            this@LikeButton.scaleY = it.animatedValue as Float
        }
        animator.start()
    }

    fun check() {
        isChecked = true
        setImageDrawable(drawables[1])
        onCheckedListener?.onChanged(true)
    }

    fun unCheck() {
        isChecked = false
        setImageDrawable(drawables[0])
        onCheckedListener?.onChanged(false)
    }

    fun isChecked() = isChecked


    interface OnCheckedListener {
        fun onChanged(isChecked: Boolean)
    }
}