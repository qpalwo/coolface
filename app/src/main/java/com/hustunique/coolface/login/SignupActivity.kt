package com.hustunique.coolface.login

import android.os.Bundle
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.bean.User
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity(R.layout.activity_signup) {

    lateinit var username: String
    lateinit var password: String

    override fun initContact() {
        super.initContact()

    }

    private fun signUp() {
        username = et_signup_username.text.toString()
        password = et_signup_password.text.toString()
        val user = BmobUser()
        user.username = username
        user.setPassword(password)
        user.signUp(object : SaveListener<User>() {
            override fun done(p0: User?, p1: BmobException?) {
                if (p1 == null) {

                } else {

                }
            }

        })

    }


}
