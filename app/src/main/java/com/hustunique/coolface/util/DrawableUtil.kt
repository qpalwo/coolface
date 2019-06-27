package com.hustunique.coolface.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap

object DrawableUtil {
    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable)
            return drawable.bitmap
        val height = drawable.intrinsicHeight
        val width = drawable.intrinsicWidth
        return drawable.toBitmap(width, height, Bitmap.Config.ARGB_8888)
    }
}