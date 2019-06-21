package com.hustunique.coolface.login

import android.content.Intent
import android.widget.Toast
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.bean.FaceBean
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.User
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity(R.layout.activity_signup) {


    override fun initContact() {
        super.initContact()
        btn_signup_signup.setOnClickListener { signUp() }

        tv_signup_login.setOnClickListener { login() }

    }

    private fun testSavePost() {
        val post = Post("URL", "message", "username", 1, FaceBean())
        post.save(object : SaveListener<String>() {
            override fun done(p0: String?, p1: BmobException?) {
                if (p1 == null) {
                    Toast.makeText(applicationContext, "保存成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, p1.errorCode.toString() + p1.message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun signUp() {
        val username = et_signup_username.text.toString()
        val password = et_signup_password.text.toString()
        val user = BmobUser()
        user.username = username
        user.setPassword(password)
        user.email = username
        user.signUp(object : SaveListener<User>() {
            override fun done(p0: User?, p1: BmobException?) {
                when {
                    p1 == null -> {
                        Toast.makeText(applicationContext, "注册成功", Toast.LENGTH_SHORT).show()
                        this@SignupActivity.finish()
                    }
                    p1.errorCode == 202 -> Toast.makeText(applicationContext, "该用户名已被占用", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(
                        applicationContext,
                        p1.errorCode.toString() + p1.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


}
