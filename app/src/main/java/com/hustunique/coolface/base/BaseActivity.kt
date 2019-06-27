package com.hustunique.coolface.base

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

abstract class BaseActivity(@LayoutRes val layoutId: Int, private val viewModelClass: Class<out ViewModel>? = null) :
    AppCompatActivity() {
    var viewModel: ViewModel? = null

    val DATA = "data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        viewModel = viewModelClass?.let { ViewModelProviders.of(this).get(viewModelClass) }

        init()

        initData(savedInstanceState)

        initView()

        initContact()
    }

    fun startActivity(targetClass: Class<out BaseActivity>, data: Bundle? = null, options: Bundle? = null) {
        val intent = Intent(this, targetClass)
        intent.putExtra(DATA, data)
        startActivity(intent, options)
    }

    fun getDataBundle(): Bundle {
        return intent.getBundleExtra(DATA)
    }

    fun toast(content: String) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }

    /**
     * 第一个调用的初始化方法
     */
    open fun init() {}

    /**
     * 用于初始化数据，如获取Intent、saveInstanceState
     */
    open fun initData(savedInstanceState: Bundle?) {}


    /**
     * 初始化View
     */
    open fun initView() {}


    /**
     * 用于初始化MVVM绑定关系
     */
    open fun initContact() {}
}