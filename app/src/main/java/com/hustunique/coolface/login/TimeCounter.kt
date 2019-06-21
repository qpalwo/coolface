package com.hustunique.coolface.login

import android.os.CountDownTimer
import android.widget.Button


class TimeCounter(private val button: Button, millisInFuture: Long, countDownInterval: Long)
    : CountDownTimer(millisInFuture, countDownInterval) {

    override fun onTick(millisUntilFinished: Long) {
        button.isClickable = false
        val btnText = (millisUntilFinished / 1000).toString() + "秒后可重新发送"
        button.text = btnText
    }

    override fun onFinish() {
        button.isClickable = true
        button.text = "重新发送"
    }

}