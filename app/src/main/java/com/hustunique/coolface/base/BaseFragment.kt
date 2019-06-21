package com.hustunique.coolface.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

abstract class BaseFragment(@LayoutRes val layoutId: Int, val viewModelClass: Class<out ViewModel>? = null) :
    Fragment() {
    lateinit var viewModel: ViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = viewModelClass?.let { ViewModelProviders.of(this).get(viewModelClass) }!!
        val view = inflater.inflate(layoutId, container, false)
        init()

        initData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)

        initContact(view.context)
    }

    open fun init() {

    }

    open fun initData() {

    }

    open fun initView(view: View) {

    }

    open fun initContact() {

    }

    open fun initContact(context: Context?) {

    }
}