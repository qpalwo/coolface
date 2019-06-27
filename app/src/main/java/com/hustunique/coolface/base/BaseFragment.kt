package com.hustunique.coolface.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

abstract class BaseFragment(@LayoutRes val layoutId: Int, val viewModelClass: Class<out ViewModel>? = null) :
    Fragment() {
    var viewModel: ViewModel? = null
    val DATA = "data"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = viewModelClass?.let { ViewModelProviders.of(this).get(viewModelClass) }
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

    fun startActivity(targetClass: Class<out BaseActivity>, data: Bundle? = null, options: Bundle? = null) {
        val intent = Intent(activity, targetClass)
        intent.putExtra(DATA, data)
        startActivity(intent, options)
    }

    fun toast(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    open fun init() {

    }

    open fun initData() {

    }

    open fun initView(view: View) {

    }

    open fun initContact(context: Context?) {

    }
}