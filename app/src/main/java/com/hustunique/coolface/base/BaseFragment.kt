package com.hustunique.coolface.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

class BaseFragment(@LayoutRes val layoutId: Int, viewModelClass: Class<out ViewModel>? = null) : Fragment() {
    val viewModel = viewModelClass?.let { ViewModelProviders.of(this).get(viewModelClass) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId, container, false)
        init()

        initData()

        initView(view)

        initContact()

        return view
    }

    open fun init() {

    }

    open fun initData() {

    }

    open fun initView(view: View) {

    }

    open fun initContact() {

    }
}