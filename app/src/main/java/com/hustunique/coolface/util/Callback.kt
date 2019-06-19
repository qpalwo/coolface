package com.hustunique.coolface.util

interface Callback<Result> {
    fun onResponse(result: Result?)
}