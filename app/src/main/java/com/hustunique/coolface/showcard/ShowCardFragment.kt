package com.hustunique.coolface.showcard

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.transition.ChangeTransform
import android.transition.Fade
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hustunique.coolface.R
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.show.BaseShowFragment
import com.hustunique.coolface.view.DragCardView
import kotlinx.android.synthetic.main.fra_show_card.*
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.android.DanmakuContext

class ShowCardFragment : BaseShowFragment(R.layout.fra_show_card, ShowCardViewModel::class.java) {
    private lateinit var post: Post

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
        setupEnterExitAni()
        postponeEnterTransition()
        Glide.with(this).load(post.imageUrl).addListener(object : RequestListener<Drawable> {
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
                fra_show_card_image.setImageDrawable(resource)
                startPostponedEnterTransition()
                return true
            }
        }).into(fra_show_card_image)

        fra_show_card_score.typeface = Typeface.createFromAsset(activity?.assets, "score.ttf")

        dmContext = mViewModel.getDmContext()
    }

    private fun setupEnterExitAni() {
        val fade = Fade()
        fade.duration = 500
        activity?.window?.enterTransition = fade
        activity?.window?.exitTransition = fade
        activity?.window?.sharedElementExitTransition = ChangeTransform()

    }

    override fun initContact(context: Context?) {
        super.initContact()
        dragCardView.setFinishCallback(object : DragCardView.FinishCallback {
            override fun onGoAway() {
                Toast.makeText(context, "离开屏幕", Toast.LENGTH_SHORT).show()
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
                }
            })
            fra_show_dm.prepare(parser, dmContext)
//            show_dm.enableDanmakuDrawingCache(true)
            Thread {
                while (!fra_show_dm.isPrepared) {
                }
                for (dm in it) {
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
                    Thread.sleep(3000)
                }
            }.start()
        })

        fra_show_card_comment_send.setOnClickListener {
            mViewModel.addDanmu(
                fra_show_card_comment.text.toString(),
                context!!,
                dmContext,
                fra_show_dm
            )
            fra_show_card_comment.setText("")
        }
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