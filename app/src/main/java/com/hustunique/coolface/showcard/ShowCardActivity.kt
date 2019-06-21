package com.hustunique.coolface.showcard

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.ChangeTransform
import android.transition.Fade
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.view.DragCardView
import kotlinx.android.synthetic.main.activity_show_card.*
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.android.DanmakuContext


class ShowCardActivity : BaseActivity(R.layout.activity_show_card, ShowCardViewModel::class.java) {
    private lateinit var post: Post

    private lateinit var dmContext: DanmakuContext

    private lateinit var mViewModel: ShowCardViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as ShowCardViewModel
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        post = intent.getSerializableExtra(getString(R.string.post)) as Post
        mViewModel.init(null)
    }

    override fun initView() {
        super.initView()
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
                show_card_image.setImageDrawable(resource)
                startPostponedEnterTransition()
                return true
            }
        }).into(show_card_image)

        show_card_score.typeface = Typeface.createFromAsset(assets, "score.ttf")

        dmContext = mViewModel.getDmContext()
    }

    private fun setupEnterExitAni() {
        val fade = Fade()
        fade.duration = 500
        window.enterTransition = fade
        window.exitTransition = fade
        window.sharedElementExitTransition = ChangeTransform()

    }

    override fun initContact() {
        super.initContact()
        show_card.setFinishCallback(object : DragCardView.FinishCallback {
            override fun onGoAway() {
                Toast.makeText(this@ShowCardActivity, "离开屏幕", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        show_back.setOnClickListener {
            supportFinishAfterTransition()
        }

        mViewModel.comments.observe(this, Observer {
            val parser = mViewModel.getParser()
            show_dm.setCallback(object : DrawHandler.Callback {
                override fun drawingFinished() {
                    show_dm.pause()
                }

                override fun danmakuShown(danmaku: BaseDanmaku?) {}

                override fun updateTimer(timer: DanmakuTimer?) {}

                override fun prepared() {
                    show_dm.start()
                }
            })
            show_dm.prepare(parser, dmContext)
//            show_dm.enableDanmakuDrawingCache(true)
            Thread {
                while (!show_dm.isPrepared) {
                }
                for (dm in it) {
                    mViewModel.showDanmu(
                        mViewModel.createDanmu(
                            this@ShowCardActivity,
                            dmContext,
                            show_dm.currentTime,
                            dm
                        ), show_dm
                    )
                    Thread.sleep(3000)
                }
            }.start()
        })

        show_card_comment_send.setOnClickListener {
            mViewModel.addDanmu(
                show_card_comment.text.toString(),
                this@ShowCardActivity,
                dmContext,
                show_dm
            )
            show_card_comment.setText("")
        }
    }


    override fun onResume() {
        super.onResume()
        if (show_dm != null && show_dm.isPrepared)
            show_dm.resume()
    }

    override fun onPause() {
        super.onPause()
        if (show_dm != null && show_dm.isPrepared) {
            show_dm.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (show_dm != null) {
            show_dm.release()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (show_dm != null) {
            show_dm.release()
        }
    }

}