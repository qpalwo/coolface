package com.hustunique.coolface.view

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import com.hustunique.coolface.R


class BoundAnimationLayout(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var boundAnimation: AnimatorSet = AnimatorSet()

    private var isPlayAnimation: Boolean = false

    private var animationProgress: Float = 0f

    private var duration: Long? = 2000

    private var rectOrCircle: Boolean = true

    private var isFirstDraw = true

    private var paint: Paint = Paint()

    private var strokeColor: Int? = Color.RED
    private var strokeWidth: Float? = 30f

    /**
     * 记录达到多少进度值时需要换边
     */
    private lateinit var progressPoint: List<Int>

    /**
     * 记录现在已经画到哪里,
     *      0 top
     *      1 right
     *      2 bottom
     *      3 left
     */
    private var nowBound: Int = 0

    private var rectWidth: Float = 0f
    private var rectHeight: Float = 0f

    private var paddingLeft = -1f
    private var paddingRight = -1f
    private var paddingTop = -1f
    private var paddingBottom = -1f

    private var path: Path = Path()

    var isLoop: Boolean = false

    init {
        initAttr(attrs)

        initDrawSetting()
    }

    private fun initAttr(attrs: AttributeSet?) {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.BoundAnimationLayout)
        strokeColor =
            context?.getColor(R.color.colorPrimaryDark)?.let {
                ta?.getColor(
                    R.styleable.BoundAnimationLayout_boundColor,
                    it
                )
            }
        strokeWidth = ta?.getFloat(R.styleable.BoundAnimationLayout_boundStrokeWidth, 30f)
        duration = ta?.getInt(R.styleable.BoundAnimationLayout_boundAnimationDuration, 3000)?.toLong()

        initPadding(ta)
    }

    /**
     * 初始化从attr来的配置信息
     */
    private fun initPadding(ta: TypedArray?) {
        val pLeft = ta?.getFloat(R.styleable.BoundAnimationLayout_boundPaddingLeft, -1f)
        val pRight = ta?.getFloat(R.styleable.BoundAnimationLayout_boundPaddingRight, -1f)
        val pTop = ta?.getFloat(R.styleable.BoundAnimationLayout_boundPaddingTop, -1f)
        val pBottom = ta?.getFloat(R.styleable.BoundAnimationLayout_boundPaddingBottom, -1f)
        val p = ta?.getFloat(R.styleable.BoundAnimationLayout_boundPadding, -1f)
        val pVer = ta?.getFloat(R.styleable.BoundAnimationLayout_boundPaddingVer, -1f)
        val pHor = ta?.getFloat(R.styleable.BoundAnimationLayout_boundPaddingHor, -1f)
        paddingLeft = if (!pLeft!!.equals(-1f)) {
            pLeft
        } else if (!pHor!!.equals(-1f)) {
            pHor
        } else if (!p!!.equals(-1f)) {
            p
        } else {
            0f
        }

        paddingRight = if (!pRight!!.equals(-1f)) {
            pRight
        } else if (!pHor!!.equals(-1f)) {
            pHor
        } else if (!p!!.equals(-1f)) {
            p
        } else {
            0f
        }

        paddingTop = if (!pTop!!.equals(-1f)) {
            pTop
        } else if (!pVer!!.equals(-1f)) {
            pVer
        } else if (!p!!.equals(-1f)) {
            p
        } else {
            0f
        }


        paddingBottom = if (!pBottom!!.equals(-1f)) {
            pBottom
        } else if (!pVer!!.equals(-1f)) {
            pVer
        } else if (!p!!.equals(-1f)) {
            p
        } else {
            0f
        }

    }

    /**
     * 初始化绘制有关的数据结构
     */
    private fun initDrawSetting() {
        setBackgroundColor(Color.TRANSPARENT)
        paint.strokeWidth = strokeWidth!!
        paint.style = Paint.Style.STROKE
        paint.color = strokeColor!!
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        initPath()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (isFirstDraw) {
            caculateProgress()
            isFirstDraw = false
        }
        if (isPlayAnimation) {
            if (rectOrCircle) {
                drawRect(canvas)
            } else {
                drawCircle()
            }
        }
        if (animationProgress >= 100) {
            resetAnimation()
            if (isLoop) {
                playAnimation()
            }
        }
    }

    private fun initAnimation() {
        val moveAnimator = ValueAnimator.ofFloat(0f, 5000f).setDuration(duration!!).apply {
            addUpdateListener {
                animationProgress = it.animatedValue as Float / 50
                postInvalidate()
            }
        }
        boundAnimation.playTogether(moveAnimator)
    }

    private fun drawCircle() {
    }

    /**
     * 渐进绘制边框
     */
    private fun drawRect(canvas: Canvas?) {
        var targetX = 0f
        var targetY = 0f
        when (nowBound) {
            0 -> {
                targetX = paddingLeft + rectWidth * animationProgress / progressPoint[0]
                targetY = paddingTop
                if (targetX >= paddingLeft + rectWidth) {
                    // 需要做修正，否则边角不平整
                    targetX = paddingLeft + rectWidth
                    nowBound = 1
                }
            }
            1 -> {
                targetX = rectWidth + paddingLeft
                targetY =
                    paddingTop + rectHeight * (animationProgress - progressPoint[0]) / (progressPoint[1] - progressPoint[0])
                if (targetY >= paddingTop + rectHeight) {
                    nowBound = 2
                    targetY = paddingTop + rectHeight
                }
            }
            2 -> {
                targetX =
                    paddingLeft + rectWidth - rectWidth * (animationProgress - progressPoint[1]) / (progressPoint[2] - progressPoint[1])
                targetY = rectHeight + paddingTop
                if (targetX <= paddingLeft) {
                    nowBound = 3
                    targetX = paddingLeft
                }
            }
            3 -> {
                targetX = paddingLeft
                targetY =
                    paddingTop + rectHeight - rectHeight * (animationProgress - progressPoint[2]) / (progressPoint[3] - progressPoint[2])
                if (animationProgress >= 100) {
                    targetX = paddingLeft
                    targetY = paddingTop
                    nowBound = 0
                }
            }
        }
//        Log.i("Bound", "targetXY $targetX : $targetY")
        canvas!!.drawPath(path.apply {
            lineTo(
                targetX,
                targetY
            )
        }, paint)
    }

    /**
     * 计算宽度长度和进度
     */
    private fun caculateProgress() {
        rectWidth = width - paddingLeft - paddingRight
        rectHeight = height - paddingTop - paddingBottom

        children.forEach {
            if (it is ImageView) {
                rectWidth = it.drawable.bounds.width() - paddingLeft - paddingRight
                rectHeight = it.drawable.bounds.height() - paddingTop - paddingBottom
            }
        }

        val dis = 2 * (rectWidth + rectHeight)
        val horPro = rectWidth / dis * 100
        val verPro = rectHeight / dis * 100

        progressPoint =
            listOf(horPro, horPro + verPro, horPro + horPro + verPro, horPro + horPro + verPro + verPro) as List<Int>
    }

    /**
     * 调用这个方法开始加载
     */
    fun playAnimation() {
        isPlayAnimation = true
        initAnimation()
        boundAnimation.start()
    }

    /**
     * 停止加载
     */
    fun pauseAnimation() {
        resetAnimation()
        boundAnimation.pause()
    }


    /**
     * 初始化路径
     */
    fun initPath() {
        path.reset()
        path.setLastPoint(paddingLeft, paddingTop)
    }


    fun setPadding(paddingLeft: Float, paddingRight: Float, paddingTop: Float, paddingBottom: Float) {
        this.paddingLeft = paddingLeft
        this.paddingRight = paddingRight
        this.paddingTop = paddingTop
        this.paddingBottom = paddingBottom
        isFirstDraw = true
        initPath()
    }

    /**
     * 初始化绘制
     */
    fun resetAnimation() {
        animationProgress = 0f
        initPath()
        isPlayAnimation = false
    }
}