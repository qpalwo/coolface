package com.hustunique.coolface

import android.os.Bundle
import com.hustunique.coolface.base.BaseActivity

class MainActivity : BaseActivity(R.layout.activity_main, MainViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
