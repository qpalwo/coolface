package com.hustunique.coolface.util

import androidx.lifecycle.MutableLiveData
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.bean.Status

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/22/19
 */
object LiveDataUtil {
    fun <T> useData(
        data: MutableLiveData<Resource<T>>,
        success: (T?) -> Unit = {},
        loading: (T?) -> Unit = {},
        error: (String?, T?) -> Unit = { s, d ->
        }
    ) {
        data.value?.let {
            when (it.status) {
                Status.SUCCESS -> {
                    success(it.data)
                }
                Status.ERROR -> {
                    error(it.message, it.data)
                }
                Status.LOADING -> {
                    loading(it.data)
                }
            }
        }
    }

    fun <T> useData(
        data: Resource<T>,
        success: (T?) -> Unit = {},
        loading: (T?) -> Unit = {},
        error: (String?, T?) -> Unit = { s, d ->
        }
    ) {
        data.let {
            when (it.status) {
                Status.SUCCESS -> {
                    success(it.data)
                }
                Status.ERROR -> {
                    error(it.message, it.data)
                }
                Status.LOADING -> {
                    loading(it.data)
                }
            }
        }
    }
}

fun <T> MutableLiveData<List<T>>.add(item: T) {
    val updatedItems = this.value as MutableList
    updatedItems.add(item)
    this.value = updatedItems
}

fun <T> MutableLiveData<List<T>>.add(position: Int, item: T) {
    val updatedItems = this.value as MutableList
    updatedItems[position] = item
    this.value = updatedItems
}