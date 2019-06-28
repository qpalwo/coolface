package com.hustunique.coolface.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.PopupMenu
import android.widget.TextView
import cn.bmob.v3.BmobUser
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
import com.hustunique.coolface.login.LoginActivity
import com.hustunique.coolface.util.AnimationUtil
import com.hustunique.coolface.util.DialogUtils
import com.hustunique.coolface.util.TextUtil
import com.hustunique.coolface.view.LikeButton
import com.kongzue.dialog.v2.CustomDialog

class MainAdapter(val mViewModel: MainViewModel) : BaseAdapter<Post>(R.layout.post_item) {
    private val sharedWeights: ArrayList<View> = ArrayList()
    private val TAG = "MainAdapter"

    override fun onBindView(holder: ViewHolder, position: Int) {
        super.onBindView(holder, position)
        val post = data!![position]
        val likeCount = holder.getView<TextView>(R.id.post_like_count)
        val likeButton = holder.getView<LikeButton>(R.id.post_like_button)
        val likeAnimation = holder.getView<LottieAnimationView>(R.id.post_like_ani)

        TextUtil.setDefaultTypeface(
            likeCount,
            holder.getView(R.id.post_message),
            holder.getView(R.id.post_username)
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
                holder.getView<LottieAnimationView>(R.id.post_loading).apply {
                    pauseAnimation()
                    visibility = GONE
                }
                return false
            }
        }).into(holder.getView(R.id.post_image))

        holder.getView<TextView>(R.id.post_message).text = post.message
        holder.getView<TextView>(R.id.post_username).text = post.username
        likeCount.text = post.likeCount.toString()

        // 是否在点赞的列表里
        var like = post.likeUser?.contains(mViewModel.user.value?.username) ?: false
        likeButton.setChecked(like)

        likeButton.onCheckedListener = object : LikeButton.OnCheckedListener {
            override fun onChanged(isChecked: Boolean) {
                if (!BmobUser.isLogin()) {
                    likeButton.setChecked(!isChecked)
                    DialogUtils.showTipDialog(holder.itemView.context, "您要登录才能点赞哦", "前往登录", {
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
        if (sharedWeights.size > position)
            sharedWeights[position] = holder.itemView
        else
            sharedWeights.add(holder.itemView)

        // 设置长按删除
        holder.itemView.apply {
            setOnLongClickListener {
                if (mViewModel.status == 1) {
                    val popMenu = PopupMenu(holder.itemView.context, holder.itemView)
                    popMenu.menuInflater.inflate(R.menu.pop_delete, popMenu.menu)
                    popMenu.show()
                    var dialog: CustomDialog? = null
                    popMenu.setOnMenuItemClickListener {
                        return@setOnMenuItemClickListener if (it.title == "删除") {
                            mViewModel.deleteAt(position, {
                                dialog?.doDismiss()
                            }, {
                                dialog = DialogUtils.showProgressDialog(holder.itemView.context, "删除中。。。")
                            }, {
                                DialogUtils.showTipDialog(holder.itemView.context, "网络开小差了", "确认", {
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