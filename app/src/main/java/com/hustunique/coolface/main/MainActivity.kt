package com.hustunique.coolface.main

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(R.layout.activity_main) {
    override fun initView() {
        super.initView()
        main_list.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
    }
}
