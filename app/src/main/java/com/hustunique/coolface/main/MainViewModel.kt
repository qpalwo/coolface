package com.hustunique.coolface.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Post

class MainViewModel : ViewModel() {
    fun init() {
        posts.value = listOf(Post("a", "a", "a", 1, null))
    }

    val posts = MutableLiveData<List<Post>>()


}