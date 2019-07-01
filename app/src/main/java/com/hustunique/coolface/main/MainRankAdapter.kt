package com.hustunique.coolface.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import cn.bmob.v3.BmobUser
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.base.BaseAdapter
import com.hustunique.coolface.base.ViewHolder
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.login.LoginActivity
import com.hustunique.coolface.pk.PkFragment
import com.hustunique.coolface.show.BaseShowCard
import com.hustunique.coolface.util.AnimationUtil
import com.hustunique.coolface.util.DialogUtil
import com.hustunique.coolface.util.TextUtil
import com.hustunique.coolface.view.LikeButton

class MainRankAdapter(private var posts: List<Post>, val mViewModel: MainViewModel) :
    BaseAdapter<Post>(R.layout.item_rank_post, posts.toMutableList()) {

    private val sharedWeights = ArrayList<View>()

    override fun onBindView(holder: ViewHolder, position: Int) {
        super.onBindView(holder, position)
        val post = data!![position]
        // 是否在点赞的列表里
        var isLike = post.likeUser?.contains(mViewModel.user.value?.username) ?: false

        val rankTextView = holder.getView<TextView>(R.id.item_rank_rank)
        val usernameTextView = holder.getView<TextView>(R.id.item_rank_username)
        val likeCount = holder.getView<TextView>(R.id.item_rank_likecount)
        val scoreTextView = holder.getView<TextView>(R.id.item_rank_score)

        val likeAnimation = holder.getView<LottieAnimationView>(R.id.item_rank_like_ani)

        val likeButton = holder.getView<LikeButton>(R.id.item_rank_button)

        val image = holder.getView<ImageView>(R.id.item_rank_image)
        val pk = holder.getView<ImageView>(R.id.item_rank_pk)


        TextUtil.setDefaultTypeface(
            rankTextView,
            usernameTextView,
            likeCount,
            scoreTextView,
            holder.getView(R.id.textView)
        )

        Glide.with(holder.itemView.context).load(post.faceBean.faceUrl).into(image)
        usernameTextView.text = post.username
        rankTextView.text = "${data!!.indexOf(post) + 1}"
        likeCount.text = post.likeCount.toString()
        scoreTextView.text = post.face!!.attributes.beauty.let {
            if (it.female_score > it.male_score)
                it.female_score.toString()
            else
                it.male_score.toString()
        }

        likeButton.setChecked(isLike)
        likeButton.onCheckedListener = object : LikeButton.OnCheckedListener {
            override fun onChanged(isChecked: Boolean) {
                if (!BmobUser.isLogin()) {
                    likeButton.setChecked(!isChecked)
                    DialogUtil.showTipDialog(holder.itemView.context, "您要登录才能PK哦", "前往登录", {
                        holder.itemView.context.startActivity(
                            Intent(
                                holder.itemView.context,
                                LoginActivity::class.java
                            )
                        )
                    }, "返回", {
                        it.doDismiss()
                    })
                    return
                }
                if (isChecked && !isLike) {
                    likeAnimation.apply {
                        AnimationUtil.lottiePlayOnce(this)
                        visibility = View.VISIBLE
                        playAnimation()
                    }
                    likeCount.text = "${++post.likeCount}"
                    isLike = true

                    mViewModel.like(position) {
                        likeButton.setChecked(false)
                        likeCount.text = "${--post.likeCount}"
                        isLike = false
                    }
                } else if (!isChecked && isLike) {
                    likeButton.setChecked(false)
                    likeCount.text = "${--post.likeCount}"
                    isLike = false
                    mViewModel.unLike(position) {
                        likeButton.setChecked(true)
                        likeCount.text = "${++post.likeCount}"
                        isLike = true
                    }
                }
            }
        }
        pk.setOnClickListener {
            if (!BmobUser.isLogin()) {
                DialogUtil.showTipDialog(holder.itemView.context, "您要登录才能点赞哦", "前往登录", {
                    holder.itemView.context.startActivity(
                        Intent(
                            holder.itemView.context,
                            LoginActivity::class.java
                        )
                    )
                }, "返回", {
                    it.doDismiss()
                })
            } else {
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        holder.itemView.context as MainActivity,
                        sharedWeights[position],
                        holder.itemView.context!!.getString(R.string.image_shared)
                    )
                BaseShowCard.start(
                    holder.itemView.context as BaseActivity,
                    PkFragment(),
                    Bundle().apply {
                        putSerializable(
                            holder.itemView.context.getString(R.string.post),
                            this@MainRankAdapter.data!![position]
                        )
                    }, options.toBundle(), MainActivity.PK_POST
                )
            }
        }

        if (sharedWeights.size > position)
            sharedWeights[position] = holder.getView(R.id.item_rank_card)
        else
            sharedWeights.add(holder.getView(R.id.item_rank_card))
    }

    fun getSharedWeight(position: Int): View {
        return sharedWeights[position]
    }


}