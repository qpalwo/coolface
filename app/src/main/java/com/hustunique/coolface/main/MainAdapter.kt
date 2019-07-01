package com.hustunique.coolface.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import cn.bmob.v3.BmobUser
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
import com.hustunique.coolface.util.PopMenuUtil
import com.hustunique.coolface.util.TextUtil
import com.hustunique.coolface.view.LikeButton
import com.kongzue.dialog.v2.CustomDialog

class MainAdapter(val mViewModel: MainViewModel, data: List<Post>? = null) :
    BaseAdapter<Post>(R.layout.item_main_post, data?.toMutableList()) {
    private val sharedWeights: ArrayList<View> = ArrayList()
    private val TAG = "MainAdapter"

    override fun onBindView(holder: ViewHolder, position: Int) {
        super.onBindView(holder, position)
        val post = data!![position]
        val likeCount = holder.getView<TextView>(R.id.post_like_count)
        val likeButton = holder.getView<LikeButton>(R.id.post_like_button)
        val likeAnimation = holder.getView<LottieAnimationView>(R.id.post_like_ani)
        val loadingAnimation = holder.getView<LottieAnimationView>(R.id.post_loading)
        val message = holder.getView<TextView>(R.id.post_message)
        val username = holder.getView<TextView>(R.id.post_username)
        val image = holder.getView<ImageView>(R.id.post_image)
        val pk = holder.getView<ImageView>(R.id.post_pk)

        TextUtil.setDefaultTypeface(
            likeCount,
            message,
            username
        )

        Glide.with(holder.itemView.context).load(post.faceBean.faceUrl).addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {

                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                loadingAnimation.apply {
                    pauseAnimation()
                    visibility = GONE
                }
                return false
            }
        }).into(image)

        message.text = post.message
        username.text = post.username
        likeCount.text = post.likeCount.toString()

        // 是否在点赞的列表里
        var like = post.likeUser?.contains(mViewModel.user.value?.username) ?: false
        likeButton.setChecked(like)

        likeButton.onCheckedListener = object : LikeButton.OnCheckedListener {
            override fun onChanged(isChecked: Boolean) {
                if (!BmobUser.isLogin()) {
                    likeButton.setChecked(!isChecked)
                    DialogUtil.showTipDialog(holder.itemView.context, "您要登录才能点赞哦", "前往登录", {
                        it.doDismiss()
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
                            this@MainAdapter.data!![position]
                        )
                    }, options.toBundle(), MainActivity.PK_POST
                )
            }
        }

        if (sharedWeights.size > position)
            sharedWeights[position] = holder.getView(R.id.post_card)
        else
            sharedWeights.add(holder.getView(R.id.post_card))

        // 设置长按删除
        holder.itemView.apply {
            setOnLongClickListener {
                if (mViewModel.status == 1) {
                    var dialog: CustomDialog? = null
                    PopMenuUtil.pop(holder.itemView.context, holder.itemView, R.menu.pop_delete)
                        .setOnMenuItemClickListener {
                            return@setOnMenuItemClickListener if (it.title == "删除") {
                                mViewModel.deleteAt(holder.itemView.context as MainActivity, position, {
                                    dialog?.doDismiss()
                                }, {
                                    dialog = DialogUtil.showProgressDialog(holder.itemView.context, "删除中。。。")
                                }, {
                                    DialogUtil.showTipDialog(holder.itemView.context, "网络开小差了", "确认", {
                                        it.doDismiss()
                                    })
                                })
                                true
                            } else {
                                false
                            }
                        }
                    return@setOnLongClickListener true
                }
                return@setOnLongClickListener false
            }
        }
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