package com.hustunique.coolface.login

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.bean.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(R.layout.activity_login) {

    lateinit var username: String
    lateinit var password: String

    override fun initContact() {
        super.initContact()

    }


    private fun login() {
        username = et_login_username.text.toString()
        password = et_login_password.text.toString()
        val user = BmobUser()
        user.username = username
        user.setPassword(password)
        user.login(object : SaveListener<User>() {
            override fun done(p0: User?, p1: BmobException?) {
                if (p1 == null) {
                    // 登录成功
                } else {
                    // 登录失败
                }
            }

        })
    }


}
