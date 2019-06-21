package com.hustunique.coolface.main

import android.animation.Animator
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseAdapter
import com.hustunique.coolface.base.ViewHolder
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.view.LikeButton

class MainAdapter : BaseAdapter<Post>(R.layout.post_item) {
    private val sharedWeights: ArrayList<ImageView> = ArrayList()

    override fun onBindView(holder: ViewHolder, position: Int) {
        super.onBindView(holder, position)
        val post = data!![position]
        Glide.with(holder.itemView.context).load(post.imageUrl)
            .into(holder.getView(R.id.post_image))
        holder.getView<TextView>(R.id.post_message).text = post.message
        holder.getView<TextView>(R.id.post_username).text = post.username
        holder.getView<TextView>(R.id.post_like_count).text = post.likeCount.toString()
        holder.getView<LikeButton>(R.id.post_like_button).onCheckedListener = object : LikeButton.OnCheckedListener {
            override fun onChanged(isChecked: Boolean) {
                if (isChecked) {
                    holder.getView<LottieAnimationView>(R.id.post_like_ani).apply {
                        addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                visibility = INVISIBLE
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {

                            }

                        })
                        visibility = VISIBLE
                        playAnimation()
                    }
                } else {

                }
            }

        }
        // TODO: 是否已经点赞还需要统计

        sharedWeights.add(position, holder.getView(R.id.post_card))
    }

    fun getSharedWeight(position: Int): View {
        return sharedWeights[position]
    }

}