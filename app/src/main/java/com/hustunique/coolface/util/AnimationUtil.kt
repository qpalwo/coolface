package com.hustunique.coolface.util

import android.animation.Animator
import android.view.View
import android.view.View.VISIBLE
import com.airbnb.lottie.LottieAnimationView

object AnimationUtil {
    fun lottiePlayOnce(vararg lottieView: LottieAnimationView) {
        lottieView.forEach {
            it.addAnimatorListener(LottieOncePlayListener(it))
        }
    }

    class LottieOncePlayListener(private val view: LottieAnimationView) : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.visibility = View.GONE
            view.pauseAnimation()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    }
}