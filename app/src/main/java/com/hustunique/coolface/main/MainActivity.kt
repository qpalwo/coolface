package com.hustunique.coolface.main

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.bmob.v3.Bmob
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.login.LoginActivity
import com.hustunique.coolface.login.SignupActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(R.layout.activity_main, MainViewModel::class.java) {

    override fun init() {
        super.init()
        (viewModel as MainViewModel).init()
        Bmob.initialize(this, "12087a50147473005dcbe686a04bf4f1")
    }

    override fun initView() {
        super.initView()
        main_list.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        main_list.adapter = MainAdapter()
    }


    override fun initContact() {
        super.initContact()
        (viewModel as MainViewModel).posts.observe(this, Observer {
            (main_list.adapter as MainAdapter).data = it
            (main_list.adapter as MainAdapter).notifyDataSetChanged()
        })

        main_me.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
