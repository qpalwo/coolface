package com.hustunique.coolface.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.util.TextUtil
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity(R.layout.activity_signup) {

    lateinit var context: Context

    override fun initContact() {
        super.initContact()
        context = applicationContext
        btn_signup_signup.setOnClickListener { signUp() }
        tv_signup_login.setOnClickListener { login() }
        tv_signup_cancel.setOnClickListener { this.finish() }

        addTextWatcher()
    }

    private fun addTextWatcher() {
        et_signup_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtil.isEmail(et_signup_username.text.toString())) {
                    btn_signup_signup.isClickable = false
                    et_signup_username.setHelperText("请输入正确的邮箱格式！")
                } else {
                    btn_signup_signup.isClickable = true
                    et_signup_username.setHelperText(null)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        et_signup_nickname.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (!TextUtil.isNickname(et_signup_nickname.text.toString())) {
                    et_signup_nickname.setHelperText("昵称不能为空！")
                    btn_signup_signup.isClickable = false
                } else {
                    et_signup_nickname.setHelperText(null)
                    btn_signup_signup.isClickable = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        et_signup_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtil.isPassword(et_signup_password.text.toString())) {
                    btn_signup_signup.isClickable = false
                    et_signup_password.setHelperText("密码不得少于六位！")
                } else {
                    btn_signup_signup.isClickable = true
                    et_signup_password.setHelperText(null)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun signUp() {
        val username = et_signup_username.text.toString()
        val nickname = et_signup_nickname.text.toString()
        val password = et_signup_password.text.toString()
        val user = BmobUser()
        user.username = username
        user.setPassword(password)
        user.email = username
        user.setValue("nickname", nickname)
        user.signUp(object : SaveListener<User>() {
            override fun done(p0: User?, p1: BmobException?) {
                Log.d("bmob", p1?.errorCode.toString() + p1?.message)
                when (p1?.errorCode) {
                    null -> {
                        Toast.makeText(context, "注册成功，已自动登录", Toast.LENGTH_SHORT).show()
                        this@SignupActivity.setResult(Activity.RESULT_OK)
                        this@SignupActivity.finish()
                    }
                    202 -> Toast.makeText(context, "该邮箱已被占用", Toast.LENGTH_SHORT).show()
                    else -> {
                        Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }


}
