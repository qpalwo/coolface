package com.hustunique.coolface.main

import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseAdapter
import com.hustunique.coolface.base.ViewHolder
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.util.AnimationUtil
import com.hustunique.coolface.view.LikeButton

class MainAdapter(val mViewModel: MainViewModel) : BaseAdapter<Post>(R.layout.post_item) {
    private val sharedWeights: ArrayList<ImageView> = ArrayList()
    private val TAG = "MainAdapter"

    override fun onBindView(holder: ViewHolder, position: Int) {
        super.onBindView(holder, position)
        val post = data!![position]
        val likeCount = holder.getView<TextView>(R.id.post_like_count)
        val likeButton = holder.getView<LikeButton>(R.id.post_like_button)
        val likeAnimation = holder.getView<LottieAnimationView>(R.id.post_like_ani)

        Glide.with(holder.itemView.context).load(post.faceBean.faceUrl).into(holder.getView(R.id.post_image))
        holder.getView<TextView>(R.id.post_message).text = post.message
        holder.getView<TextView>(R.id.post_username).text = post.username
        likeCount.text = post.likeCount.toString()

        // 是否在点赞的列表里
        var like = post.likeUser?.contains("testuser") ?: false
        likeButton.setChecked(like)

        likeButton.onCheckedListener = object : LikeButton.OnCheckedListener {
            override fun onChanged(isChecked: Boolean) {
                Log.i(TAG, "isChecked: $isChecked | like：$like")
                if (isChecked && !like) {
                    likeAnimation.apply {
                        AnimationUtil.lottiePlayOnce(this)
                        visibility = VISIBLE
                        playAnimation()
                    }
                    likeCount.text = "${++post.likeCount}"
                    like = true
                    Log.i(TAG, "$position like： $like $isChecked")

                    mViewModel.like(position) {
                        likeButton.setChecked(false)
                        likeCount.text = "${--post.likeCount}"
                        like = false
                        Log.i(TAG, "like faild")
                    }
                } else if (!isChecked && like) {
                    likeButton.setChecked(false)
                    likeCount.text = "${--post.likeCount}"
                    like = false
                    mViewModel.unLike(position) {
                        likeButton.setChecked(true)
                        likeCount.text = "${++post.likeCount}"
                        like = true
                    }
                }
            }
        }
        sharedWeights.add(position, holder.getView(R.id.post_card))
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.getView<LottieAnimationView>(R.id.post_like_ani).apply {
            pauseAnimation()
            visibility = GONE
        }
    }

    fun getSharedWeight(position: Int): View {
        return sharedWeights[position]
    }
}