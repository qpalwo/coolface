package com.hustunique.coolface.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.VelocityTracker
import android.view.WindowManager
import androidx.cardview.widget.CardView

class DragCardView(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {
    private var isMoving: Boolean = false
    private var startX = 0f
    private var startY = 0f

    /**
     * DOWN事件时的锚点
     */
    private var firstPx = 0f
    private var firstPy = 0f

    private var lastX = 0f
    private var lastY = 0f

    /**
     * 宣传的半径
     */
    private var rotationR: Float = 0f

    /**
     * onTouchEvent中DOWN的时间
     */
    private var startTime: Long = 0

    /**
     * 屏幕宽高
     */
    private var display: Point? = null

    /**
     * 速度监听器
     */
    private val speedTracker: VelocityTracker = VelocityTracker.obtain()


    /**
     * 是否竖直
     */
    private var isVer = false

    /**
     * 是否在向左滑
     */
    private var isLeft = false


    /**
     * 是否在向右划
     */
    private var isUp = false

    /**
     * 用于监听卡片是否被抛出
     */
    private var finishCallback: FinishCallback? = null

    init {
        firstPx = pivotX
        firstPy = pivotY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        speedTracker.addMovement(event)
        when (event?.action) {
            ACTION_DOWN -> {
                startX = event.rawX
                lastX = startX
                startY = event.rawY
                lastY = startY
                startTime = System.currentTimeMillis()

                pivotY = top.toFloat() + 100
                pivotX = left.toFloat() + 100

                rotationR = dis(left.toFloat() + 30, top.toFloat() + 30, startX, startY)
            }
            ACTION_MOVE -> {
                val cX = event.rawX - lastX
                val cY = event.rawY - lastY
                lastX = event.rawX
                lastY = event.rawY

                // 透明度
                if (isFinish(event.rawX, event.rawY)) {
                    alpha = 0.6f
                } else {
                    alpha = 1f
                }

                // 旋转
                pivotY = top.toFloat() + 100
                pivotX = left.toFloat() + 100

                isVer = Math.abs(cX) < Math.abs(cY)

                val smallB = if (isVer) cX else cY
                val arc = dis(0f, smallB, smallB, 0f)
                val degree = Math.toDegrees(arc * 180 / (rotationR * Math.PI)).toFloat()
                rotation += (if (cX > 0) -degree else degree) / 20

                // 平移
                translationX += cX
                translationY += cY

                isMoving = true
            }
            ACTION_UP -> {
                speedTracker.computeCurrentVelocity(1000)
                val speed =
                    Math.sqrt(speedTracker.xVelocity * speedTracker.xVelocity + speedTracker.yVelocity * speedTracker.yVelocity.toDouble())
                if (isMoving && (isFinish(event.rawX, event.rawY) || speed >= 2000)) {
                    isVer = Math.abs(speedTracker.yVelocity) > Math.abs(speedTracker.xVelocity)
                    isLeft = speedTracker.xVelocity < 0
                    isUp = speedTracker.yVelocity < 0

                    val animatorSet = AnimatorSet()
                    animatorSet.duration = 500
                    val objectAnimator1 = ObjectAnimator.ofFloat(0f, 10f).setDuration(500)

                    objectAnimator1.addUpdateListener {
                        val value = if (isLeft) it.animatedValue as Float else -(it.animatedValue as Float)
                        rotation += value
                    }
                    val objectAnimator2 = ObjectAnimator.ofFloat(0f, 200f).setDuration(1000)
                    objectAnimator2.addUpdateListener {
                        if (isVer) {
                            translationY += if (isUp) {
                                -(it.animatedValue as Float)
                            } else {
                                it.animatedValue as Float
                            }
                        } else {
                            translationX += if (isLeft) {
                                -(it.animatedValue as Float)
                            } else {
                                it.animatedValue as Float
                            }
                        }
                        if (it.animatedValue as Float > 190) {
                            finishCallback?.onGoAway()
                        }
                    }
                    animatorSet.playTogether(objectAnimator1, objectAnimator2)
                    animatorSet.start()
                } else {
                    val animatorSet = AnimatorSet()
                    animatorSet.duration = 500
                    val objectAnimator1 = ObjectAnimator.ofFloat(rotation, 0f).setDuration(500)

                    objectAnimator1.addUpdateListener {
                        rotation = it.animatedValue as Float
                    }
                    val objectAnimator2 = ObjectAnimator.ofFloat(translationX, 0f).setDuration(800)
                    objectAnimator2.addUpdateListener {
                        translationX = it.animatedValue as Float
                    }
                    val objectAnimator3 = ObjectAnimator.ofFloat(translationY, 0f).setDuration(800)
                    objectAnimator3.addUpdateListener {
                        translationY = it.animatedValue as Float
                    }
                    animatorSet.playTogether(objectAnimator1, objectAnimator2, objectAnimator3)
                    animatorSet.start()
                }
                isMoving = false
            }
        }
        return true
    }

    private fun dis(x1: Float, y1: Float, x2: Float, y2: Float): Float =
        Math.sqrt((y1 - y2) * (y1 - y2) + (x1 - x2) * (x1 - x2).toDouble())
            .toFloat()


    private fun isFinish(x: Float, y: Float): Boolean {
        val dw = getDisplayWH().x
        val dh = getDisplayWH().y
        return x <= 100 || x >= (dw - 100) || y <= 200 || y >= (dh - 200)
    }


    private fun getDisplayWH(): Point {
        if (display != null)
            return display!!
        display = Point()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getSize(display)
        val height = display?.y
        val width = display?.x
        return display!!
    }

    /**
     * 监听卡片是否被抛出
     */
    fun setFinishCallback(finishCallback: FinishCallback) {
        this.finishCallback = finishCallback
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        speedTracker.recycle()
    }


    interface FinishCallback {
        fun onGoAway()
    }
}