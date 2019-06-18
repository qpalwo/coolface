package com.hustunique.coolface.base

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mViews: SparseArray<View> = SparseArray()

    companion object {
        fun get(@LayoutRes layoutId: Int, parent: ViewGroup): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            return ViewHolder(view)
        }
    }

    fun <V : View> getView(@IdRes resId: Int): V {
        var view = mViews.get(resId)
        if (view == null) {
            view = itemView.findViewById(resId)
            mViews.put(resId, view)
        }
        return view as V
    }
}