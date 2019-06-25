package com.hustunique.coolface.main

import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseAdapter
import com.hustunique.coolface.base.ViewHolder
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.util.AnimationUtil
import com.hustunique.coolface.view.LikeButton

class MainAdapter(val mViewModel: MainViewModel) : BaseAdapter<Post>(R.layout.post_item) {
    private val sharedWeights: ArrayList<ImageView> = ArrayList()

    override fun onBindView(holder: ViewHolder, position: Int) {
        super.onBindView(holder, position)
        val post = data!![position]
        var like = post.likeUser?.contains("testuser") ?: false
        //todo change to true user data
//        var like = post.likeUser?.contains(BmobUser.getCurrentUser(User::class.java).username) ?: false
        Glide.with(holder.itemView.context).load(post.faceBean.faceUrl)
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    notifyDataSetChanged()
                    return false
                }
            }).into(holder.getView(R.id.post_image))
        holder.getView<TextView>(R.id.post_message).text = post.message
        holder.getView<TextView>(R.id.post_username).text = post.username
        holder.getView<TextView>(R.id.post_like_count).text = post.likeCount.toString()
        if (like) {
            holder.getView<LikeButton>(R.id.post_like_button).check()
        } else {
            holder.getView<LikeButton>(R.id.post_like_button).unCheck()
        }
        holder.getView<LikeButton>(R.id.post_like_button).onCheckedListener = object : LikeButton.OnCheckedListener {
            override fun onChanged(isChecked: Boolean) {
                if (isChecked && !like) {
                    holder.getView<LottieAnimationView>(R.id.post_like_ani).apply {
                        AnimationUtil.lottiePlayOnce(this)
                        visibility = VISIBLE
                        playAnimation()
                    }
                    holder.getView<TextView>(R.id.post_like_count).text = "${++post.likeCount}"
                    like = true
                    mViewModel.like(position) {
                        holder.getView<LikeButton>(R.id.post_like_button).unCheck()
                        holder.getView<TextView>(R.id.post_like_count).text = "${--post.likeCount}"
                        like = false
                    }
                } else if (!isChecked && like) {
                    mViewModel.unLike(position) {
                        holder.getView<LikeButton>(R.id.post_like_button).check()
                        holder.getView<TextView>(R.id.post_like_count).text = "${--post.likeCount}"
                        like = true
                    }
                }
            }
        }
        sharedWeights.add(position, holder.getView(R.id.post_card))
    }

    fun getSharedWeight(position: Int): View {
        return sharedWeights[position]
    }
}