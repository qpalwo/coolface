package com.hustunique.coolface.main

import android.widget.TextView
import com.bumptech.glide.Glide
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseAdapter
import com.hustunique.coolface.base.ViewHolder
import com.hustunique.coolface.bean.Post

class MainAdapter : BaseAdapter<Post>(R.layout.post_item) {
    override fun onBindView(holder: ViewHolder, position: Int) {
        super.onBindView(holder, position)
        val post = data!![position]
        Glide.with(holder.itemView.context).load(post.imageUrl)
            .into(holder.getView(R.id.post_image))
        holder.getView<TextView>(R.id.post_message).text = post.message
        holder.getView<TextView>(R.id.post_username).text = post.username
        holder.getView<TextView>(R.id.post_like_count).text = post.likeCount.toString()
        // TODO: 是否已经点赞还需要统计
    }
}