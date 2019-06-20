package com.hustunique.coolface.showcard

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Fade
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.view.DragCardView
import kotlinx.android.synthetic.main.activity_show.*

class ShowActivity : BaseActivity(R.layout.activity_show) {
    private lateinit var post: Post

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        post = intent.getSerializableExtra(getString(R.string.post)) as Post
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
    }

    private fun setupEnterExitAni() {
        val fade = Fade()
        fade.duration = 500
        window.enterTransition = fade
        window.exitTransition = fade
    }

    override fun initContact() {
        super.initContact()
        show_card.setFinishCallback(object : DragCardView.FinishCallback {
            override fun onGoAway() {
                Toast.makeText(this@ShowActivity, "离开屏幕", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        show_back.setOnClickListener {
            finish()
        }
    }
}