package com.hustunique.coolface.showcard

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hustunique.coolface.R
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.login.LoginActivity
import com.hustunique.coolface.picture.PictureActivity
import com.hustunique.coolface.show.BaseShowFragment
import com.hustunique.coolface.util.*
import com.hustunique.coolface.view.DragCardView
import com.hustunique.coolface.view.LikeButton
import kotlinx.android.synthetic.main.base_show_card.*
import kotlinx.android.synthetic.main.fra_show_card.*
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.android.DanmakuContext

class ShowCardFragment : BaseShowFragment(R.layout.fra_show_card, ShowCardViewModel::class.java) {
    private lateinit var dmContext: DanmakuContext

    private lateinit var mViewModel: ShowCardViewModel

    private var like = false

    private lateinit var post: Post

    override fun init() {
        super.init()
        mViewModel = viewModel as ShowCardViewModel
        val height = DisplayUtil.getHeight(getOuterActivity())
        getAnimationBound().setPadding(
            65f,
            65f,
            height / 2 - 1200f,
            height / 2 - 1200f
        )
    }

    override fun initData() {
        super.initData()
        mViewModel.init(arguments?.getSerializable(getString(R.string.post)) as Post)
    }

    override fun initView(view: View) {
        super.initView(view)
        // 先延迟进入的动画，让图片加载完再进来
        getOuterActivity().supportPostponeEnterTransition()
        dmContext = mViewModel.getDmContext()

        TextUtil.setDefaultTypeface(
            fra_show_card_message,
            fra_show_card_score,
            fra_age,
            fra_gender,
            fra_emotion,
            fra_ethnicity,
            fra_glass,
            fra_mouthstatus,
            fra_show_likecount
        )

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


        fra_show_card_comment_send.setOnClickListener {
            if (fra_show_card_comment.text.toString().isNotEmpty())
                sendDm(fra_show_card_comment.text.toString())
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

        fra_show_card_image.setOnClickListener {
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getOuterActivity(),
                    it,
                    getString(R.string.image_shared)
                )
            startActivity(PictureActivity::class.java, Bundle().apply {
                putString(PictureActivity.PICTURE, post.faceBean.faceUrl)
            }, options.toBundle())
        }

        mViewModel.postData.observe(this, Observer {
            LiveDataUtil.useData(it, { post ->
                this.post = post!!
                if (!BmobUser.isLogin()) {
                    getOuterActivity().supportStartPostponedEnterTransition()
                    DialogUtil.showTipDialog(context!!, "您没有登录", "前往登录", {
                        startActivity(LoginActivity::class.java)
                    }, "返回", {
                        getOuterActivity().supportFinishAfterTransition()
                    })
                    return@useData
                }

                like = post.likeUser?.contains(BmobUser.getCurrentUser(User::class.java).username) ?: false

                fra_show_likecount.text = post.likeCount.toString()

                fra_show_like.setChecked(like)

                fra_show_collect.setChecked(
                    post.favouriteUser?.contains(BmobUser.getCurrentUser(User::class.java).username) ?: false
                )

                Glide.with(this).load(post.faceBean.faceUrl).addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        getOuterActivity().supportStartPostponedEnterTransition()
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // 进入动画开始
                        getOuterActivity().supportStartPostponedEnterTransition()
                        return false
                    }
                }).into(fra_show_card_image)

                fra_show_card_score.text = post.face?.attributes?.beauty?.let {
                    if (it.female_score > it.male_score)
                        it.female_score.toString()
                    else
                        it.male_score.toString()
                }
                fra_show_likecount.text = post.likeCount.toString()

                fra_age.text = post.face?.attributes?.age?.value?.toString()

                fra_gender.text = post.face?.attributes?.gender?.value.toString()

                fra_ethnicity.text = post.face?.attributes!!.ethnicity.value

                fra_glass.text = post.face?.attributes!!.glass.value

                fra_mouthstatus.text = post.face?.attributes!!.mouthstatus.let {
                    when (listOf(it.close, it.open, it.surgical_mask_or_respirator, it.other_occlusion).let {
                        it.indexOf(it.max())
                    }) {
                        0 -> {
                            "Close"
                        }
                        1 -> {
                            "Open"
                        }
                        2 -> {
                            "Mask"
                        }
                        else -> {
                            "Other"
                        }
                    }
                }

                fra_emotion.text = post.face?.attributes!!.emotion.let {
                    when (listOf(
                        it.anger,
                        it.disgust,
                        it.fear,
                        it.happiness,
                        it.neutral,
                        it.sadness,
                        it.surprise
                    ).toMutableList().let {
                        it.indexOf(it.max())
                    }) {
                        0 -> "Angry"
                        1 -> "Disgusted"
                        2 -> "Scared"
                        3 -> "Happy"
                        5 -> "Sad"
                        6 -> "Surprise"
                        else -> "Neutral"
                    }
                }

                fra_show_card_message.text = post.message

                fra_user_nickname.text = post.username

                val parser = mViewModel.getParser()
                fra_show_dm.setCallback(object : DrawHandler.Callback {
                    override fun drawingFinished() {
                        if (!fra_show_dm.isPaused)
                            fra_show_dm.pause()
                    }

                    override fun danmakuShown(danmaku: BaseDanmaku?) {}

                    override fun updateTimer(timer: DanmakuTimer?) {}

                    override fun prepared() {
                        post.comments?.let {
                            fra_show_dm.start()
                            showDanmas(it)
                        }
                    }
                })
                fra_show_dm.prepare(parser, dmContext)
                fra_show_dm.enableDanmakuDrawingCache(true)
            })
        })
    }

    /**
     * 是否点赞
     */
    private fun likeOrNot(isLiked: Boolean) {
        if (isLiked && !like) {
            mViewModel.like {
                // onError
                toast("操作失败：$it")
                fra_show_likecount.text = (fra_show_likecount.text.toString().toInt() - 1).toString()
                fra_show_like_ani.visibility = GONE
            }
            fra_show_likecount.text = (fra_show_likecount.text.toString().toInt() + 1).toString()
        } else if (!isLiked && like) {
            mViewModel.unLike {
                toast("操作失败：$it")
                fra_show_likecount.text = (fra_show_likecount.text.toString().toInt() + 1).toString()
                fra_show_like_ani.visibility = VISIBLE
            }
            fra_show_likecount.text = (fra_show_likecount.text.toString().toInt() - 1).toString()
        }
        fra_show_like_ani.visibility = if (isLiked) {
            fra_show_like_ani.playAnimation()
            VISIBLE
        } else GONE
    }

    /**
     * 是否收藏
     */
    private fun collectOrNot(isCollected: Boolean) {
        if (isCollected) {
            mViewModel.collect {
                toast("操作失败：$it")
                fra_show_colle_ani.visibility = GONE
            }
        } else {
            mViewModel.unCollect {
                toast("操作失败：$it")
                fra_show_colle_ani.visibility = VISIBLE
            }
        }
        fra_show_colle_ani.visibility = if (isCollected) {
            fra_show_colle_ani.playAnimation()
            VISIBLE
        } else GONE
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
    fun sendDm(content: String) {
        getOuterActivity().card_bound.playAnimation()
        mViewModel.addDanmu(
            content,
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