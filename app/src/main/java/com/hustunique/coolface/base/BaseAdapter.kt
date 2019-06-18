package com.hustunique.coolface.base

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 这个是最基础的Adapter，特殊类型的要单独写
 * 泛型T写的是这个Adapter对应的Bean的类型，可以不写
 */
abstract class BaseAdapter<Data>(@LayoutRes val itemLayoutRes: Int, var data: List<Data>? = null) :
    RecyclerView.Adapter<ViewHolder>() {

    var clickListener: ListOnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        init()

        initData()

        return ViewHolder.get(itemLayoutRes, parent)
    }

    /**
     * 第一个调用的初始化
     */
    fun init() {}

    /**
     * 初始化数据
     */
    fun initData() {}


    override fun getItemCount(): Int = if (data == null) 0 else data?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            clickListener?.onClick(position, holder.itemView)
        }
    }

}