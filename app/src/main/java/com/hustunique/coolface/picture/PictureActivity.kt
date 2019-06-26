package com.hustunique.coolface.picture

import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import kotlinx.android.synthetic.main.activity_picture.*

class PictureActivity : BaseActivity(R.layout.activity_picture) {
    companion object {
        val PICTURE = "picture"
    }


    private lateinit var url: String

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        url = getDataBundle().getString(PICTURE)!!
    }

    override fun initView() {
        super.initView()
        supportPostponeEnterTransition()
        Glide.with(this).load(url).addListener(object : RequestListener<Drawable> {
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
                supportStartPostponedEnterTransition()
                return false
            }
        }).into(picture_image)
    }

    override fun initContact() {
        super.initContact()
        picture_image.setOnClickListener {
            supportFinishAfterTransition()
        }
    }
}