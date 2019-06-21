package com.hustunique.coolface.login

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.util.TextUtil
import kotlinx.android.synthetic.main.activity_reset.*

class ResetActivity : BaseActivity(R.layout.activity_reset) {

    lateinit var timeCounter: TimeCounter
    lateinit var context: Context

    override fun initContact() {
        super.initContact()
        context = applicationContext
        timeCounter = TimeCounter(btn_reset_send, 60000, 1000)
        btn_reset_send.setOnClickListener { sendResetEmail() }
        tv_reset_cancel.setOnClickListener { this.finish() }
        addTextWatcher()
    }

    private fun addTextWatcher() {
        et_reset_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtil.isEmail(et_reset_username.text.toString())) {
                    et_reset_username.setHelperText("请输入正确的邮箱！")
                    btn_reset_send.isClickable = false
                } else {
                    et_reset_username.setHelperText(null)
                    btn_reset_send.isClickable = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun sendResetEmail() {
        val email = et_reset_username.text.toString()
        if (!TextUtil.isEmail(email)) {
            Toast.makeText(this, "请输入正确的邮箱", Toast.LENGTH_SHORT).show()
            return
        }
        BmobUser.resetPasswordByEmail(email, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                when (p0?.errorCode) {
                    null -> {
                        timeCounter.start()
                        Toast.makeText(context, "已发送重置密码邮件", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

}
