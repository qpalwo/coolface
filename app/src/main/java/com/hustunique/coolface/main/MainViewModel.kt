package com.hustunique.coolface.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.model.local.PictureRepo

class MainViewModel : ViewModel() {
    fun init() {
        posts.value = listOf(
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null),
            Post("a", "a", "a", 1, null)
        )
    }

    val posts = MutableLiveData<List<Post>>()

    fun getPictureFile(context: Context) = PictureRepo.getInstance(context).getFile()
}