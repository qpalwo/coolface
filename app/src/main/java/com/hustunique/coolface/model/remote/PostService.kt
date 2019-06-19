package com.hustunique.coolface.model.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.util.Callback
import java.nio.channels.MulticastChannel

object PostService {

    /**
     * 获取所有的动态
     */
    fun getAllPost(callback: Callback<List<Post>>) {
        val query = BmobQuery<Post>()
        query.findObjects(object : FindListener<Post>() {
            override fun done(p0: MutableList<Post>?, p1: BmobException?) {
                if (p1 == null) {
                    callback.onResponse(p0)
                } else {
                    callback.onResponse(null)
                }
            }
        })
    }

    fun getPostByRange(start: Int, end: Int) {

    }

    /**
     * 存动态
     * @param post 要上传的动态
     * @param callback onResponse中返回Post
     *                  成功 上传的Post
     *                  失败 返回null
     */
    fun save(post: Post, callback: Callback<Post>) {

    }
}