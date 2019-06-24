package com.hustunique.coolface.base

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 这个是最基础的Adapter，特殊类型的要单独写
 * 泛型T写的是这个Adapter对应的Bean的类型，可以不写
 */
abstract class BaseAdapter<Data>(
    @LayoutRes val itemLayoutRes: Int, var data: List<Data>? = null
) :
    RecyclerView.Adapter<ViewHolder>() {

    var clickListener: ListOnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        init()

        initData()

        return ViewHolder.get(itemLayoutRes, parent)
    }

    override fun getItemCount(): Int = if (data == null) 0 else data?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            clickListener?.onClick(position, holder.itemView)
        }
        onBindView(holder, position)
    }


    /**
     * 第一个调用的初始化
     */
    open fun init() {}

    /**
     * 初始化数据
     */
    open fun initData() {}

    /**
     * 绑定视图的时候调用
     */
    open fun onBindView(holder: ViewHolder, position: Int) {}

}