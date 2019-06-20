package com.hustunique.coolface.login

import android.widget.Toast
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import kotlinx.android.synthetic.main.activity_reset.*

class ResetActivity : BaseActivity(R.layout.activity_reset) {

    override fun initContact() {
        super.initContact()
        btn_reset_send.setOnClickListener {
            sendResetEmail()
        }
    }

    private fun sendResetEmail() {
        val email = et_reset_username.text.toString()
        BmobUser.resetPasswordByEmail(email, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Toast.makeText(applicationContext, "已发送重置密码邮件", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, p0.errorCode.toString() + p0.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
