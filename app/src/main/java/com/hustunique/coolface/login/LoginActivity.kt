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
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(R.layout.activity_login) {

    lateinit var context: Context

    override fun initContact() {
        super.initContact()
        context = applicationContext
        btn_login_login.setOnClickListener { login() }
        tv_login_forget.setOnClickListener { forget() }
        tv_login_cancel.setOnClickListener { this.finish() }
        tv_login_signup.setOnClickListener { signup() }

        addTextWatcher()
    }

    private fun login() {
        val username = et_login_username.text.toString()
        val password = et_login_password.text.toString()

        val user = BmobUser()
        user.username = username
        user.setPassword(password)
        user.login(object : SaveListener<User>() {
            override fun done(p0: User?, p1: BmobException?) {
                Log.d("bmob", p1?.errorCode.toString() + p1?.message)
                when (p1?.errorCode) {
                    null -> {
                        Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                        this@LoginActivity.setResult(Activity.RESULT_OK)
                        this@LoginActivity.finish()
                    }
                    304 -> {
                        Toast.makeText(context, "邮箱不能为空", Toast.LENGTH_SHORT).show()
                    }
                    109 -> {
                        Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show()
                    }
                    101 -> {
                        Toast.makeText(context, "邮箱或密码错误", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    private fun addTextWatcher() {
        et_login_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtil.isEmail(et_login_username.text.toString())) {
                    et_login_username.setHelperText("请输入正确的邮箱！")
                    btn_login_login.isClickable = false
                } else {
                    et_login_username.setHelperText(null)
                    btn_login_login.isClickable = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        et_login_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtil.isNotEmpty(et_login_password.text.toString())) {
                    et_login_password.setHelperText("密码不能为空！")
                    btn_login_login.isClickable = false
                } else {
                    et_login_username.setHelperText(null)
                    btn_login_login.isClickable = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    private fun forget() {
        val intent = Intent(this, ResetActivity::class.java)
        startActivity(intent)
    }

    private fun signup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        this.finish()
    }

}
