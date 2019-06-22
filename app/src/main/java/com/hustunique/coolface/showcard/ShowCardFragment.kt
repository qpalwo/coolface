package com.hustunique.coolface.showcard

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hustunique.coolface.R
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.show.BaseShowFragment
import com.hustunique.coolface.util.AnimationUtil
import com.hustunique.coolface.util.TextUtil
import com.hustunique.coolface.view.DragCardView
import com.hustunique.coolface.view.LikeButton
import kotlinx.android.synthetic.main.fra_show_card.*
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.android.DanmakuContext

class ShowCardFragment : BaseShowFragment(R.layout.fra_show_card, ShowCardViewModel::class.java) {
    private var post: Post? = null

    private lateinit var dmContext: DanmakuContext

    private lateinit var mViewModel: ShowCardViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as ShowCardViewModel
    }

    override fun initData() {
        super.initData()
        post = arguments?.getSerializable(getString(R.string.post)) as Post
        mViewModel.init(null)
    }

    override fun initView(view: View) {
        super.initView(view)
        // 先延迟进入的动画，让图片加载完再进来
        postponeEnterTransition()
        Glide.with(this).load(post?.imageUrl).addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                // 展示所有的视图
                initShowView(resource)
                // 进入动画开始
                startPostponedEnterTransition()
                return true
            }
        }).into(fra_show_card_image)

        TextUtil.setDefaultTypeface(fra_show_card_score)

        dmContext = mViewModel.getDmContext()

        // 让动画只播放一次
        AnimationUtil.lottiePlayOnce(fra_show_like_ani, fra_show_colle_ani)
    }

    override fun initContact(context: Context?) {
        super.initContact(context)
        dragCardView.setFinishCallback(object : DragCardView.FinishCallback {
            override fun onGoAway() {
                activity?.finish()
            }
        })

        fra_show_back.setOnClickListener {
            activity?.supportFinishAfterTransition()
        }

        mViewModel.comments.observe(this, Observer {
            val parser = mViewModel.getParser()
            fra_show_dm.setCallback(object : DrawHandler.Callback {
                override fun drawingFinished() {
                    fra_show_dm.pause()
                }

                override fun danmakuShown(danmaku: BaseDanmaku?) {}

                override fun updateTimer(timer: DanmakuTimer?) {}

                override fun prepared() {
                    fra_show_dm.start()
                    showDanmas(it)
                }
            })
            fra_show_dm.prepare(parser, dmContext)
            fra_show_dm.enableDanmakuDrawingCache(true)
        })

        fra_show_card_comment_send.setOnClickListener {
            sendDm()
        }

        fra_show_like.onCheckedListener = object : LikeButton.OnCheckedListener {
            override fun onChanged(isChecked: Boolean) {
                likeOrNot(isChecked)
            }
        }

        fra_show_collect.onCheckedListener = object : LikeButton.OnCheckedListener {
            override fun onChanged(isChecked: Boolean) {
                collectOrNot(isChecked)
            }
        }


    }

    /**
     * 是否点赞
     */
    private fun likeOrNot(isLiked: Boolean) {
        fra_show_like_ani.visibility = if (isLiked) {
            fra_show_like_ani.playAnimation()
            VISIBLE
        } else GONE

        fra_show_likecount.text = (fra_show_likecount.text.toString().toInt() + if (isLiked) 1 else -1).toString()
        // TODO 修改数据结构的likecount
        post?.likeCount?.plus(1)
    }

    /**
     * 是否收藏
     */
    private fun collectOrNot(isCollected: Boolean) {
        fra_show_colle_ani.visibility = if (isCollected) {
            fra_show_colle_ani.playAnimation()
            VISIBLE
        } else GONE

        // TODO 修改数据结构的 collect
    }

    /**
     * 展示所有视图
     */
    fun initShowView(image: Drawable?) {
        fra_show_card_image.setImageDrawable(image)
        fra_show_likecount.text = post?.likeCount?.toString()
    }


    /**
     * 初始化展示弹幕
     */
    fun showDanmas(contents: List<String>) {
        Thread {
            while (!fra_show_dm.isPrepared) {
            }
            for (dm in contents) {
                if (fra_show_dm == null)
                    break
                mViewModel.showDanmu(
                    mViewModel.createDanmu(
                        context!!,
                        dmContext,
                        fra_show_dm.currentTime,
                        dm
                    ), fra_show_dm
                )
                Thread.sleep(2000)
            }
        }.start()
    }


    /**
     * 添加一条弹幕
     */
    fun sendDm() {
        mViewModel.addDanmu(
            fra_show_card_comment.text.toString(),
            context!!,
            dmContext,
            fra_show_dm
        )
        fra_show_card_comment.setText("")
    }


    override fun onResume() {
        super.onResume()
        if (fra_show_dm != null && fra_show_dm.isPrepared)
            fra_show_dm.resume()
    }

    override fun onPause() {
        super.onPause()
        if (fra_show_dm != null && fra_show_dm.isPrepared) {
            fra_show_dm.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fra_show_dm != null) {
            fra_show_dm.release()
        }
    }
}