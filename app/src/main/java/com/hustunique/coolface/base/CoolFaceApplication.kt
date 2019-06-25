package com.hustunique.coolface.base

import android.app.Application
import android.content.Context
import cn.bmob.v3.Bmob
import com.hustunique.coolface.model.remote.config.BmobConfig

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/25/19
 */
class CoolFaceApplication : Application() {
    companion object {
        lateinit var mApplicationContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        Bmob.initialize(this, BmobConfig.APPLICATION_ID)
        mApplicationContext = applicationContext
    }
}