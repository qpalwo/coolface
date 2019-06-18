package com.hustunique.coolface.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

abstract class BaseActivity(@LayoutRes val layoutId: Int, private val viewModelClass: Class<out ViewModel>? = null) :
    AppCompatActivity() {
    var viewModel: ViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        viewModel = viewModelClass?.let { ViewModelProviders.of(this).get(viewModelClass) }

        init()

        initData(savedInstanceState)

        initView()

        initContact()
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