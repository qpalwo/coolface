package com.hustunique.coolface.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

object DisplayUtil {
    var point: Point? = null

    fun getHeight(context: Context): Int {
        if (point == null) {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            point = Point()
            wm.defaultDisplay.getSize(point)
        }
        return point?.y!!
    }

    fun getWidth(context: Context): Int {
        if (point == null) {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            point = Point()
            wm.defaultDisplay.getSize(point)
        }
        return point?.x!!
    }
}