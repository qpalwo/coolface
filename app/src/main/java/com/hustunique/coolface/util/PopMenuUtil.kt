package com.hustunique.coolface.util

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes

object PopMenuUtil {
    fun pop(
        context: Context,
        targetView: View, @MenuRes menuId: Int
    ): PopupMenu {
        val popMenu = PopupMenu(context, targetView)
        popMenu.menuInflater.inflate(menuId, popMenu.menu)
        popMenu.show()
        return popMenu
    }
}