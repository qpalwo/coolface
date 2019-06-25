package com.hustunique.coolface.showscore

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.hustunique.coolface.R
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.show.BaseShowFragment
import com.hustunique.coolface.util.LiveDataUtil
import com.hustunique.coolface.util.TextUtil
import kotlinx.android.synthetic.main.fra_analy_result.*

class ShowScoreFragment : BaseShowFragment(R.layout.fra_analy_result, ShowScoreViewModel::class.java) {
    private lateinit var mViewModel: ShowScoreViewModel

    private lateinit var mImage: String

    override fun init() {
        super.init()
        mViewModel = viewModel as ShowScoreViewModel
    }

    override fun initData() {
        super.initData()
        mViewModel.init()
    }

    override fun initView(view: View) {
        super.initView(view)
        TextUtil.setDefaultTypeface(
            analy_age_tip,
            analy_sex_tip,
            analy_tip,
            analy_sex,
            analy_age,
            analy_similar_star_name,
            analy_similar_user_name,
            analy_submit,
            analy_score
        )

    }

    override fun initContact(context: Context?) {
        super.initContact(context)
        analy_submit.setOnClickListener {
            mViewModel.post(analy_post_message.text.toString())
            getAnimationBound().pauseAnimation()
            this@ShowScoreFragment.getOuterActivity().finish()
        }

        mViewModel.pictureData.observe(this,
            Observer<Resource<String>> {
                LiveDataUtil.useData(it, {
                    mImage = it!!
                    Glide.with(this)
                        .load(it)
                        .into(analy_myimage)
                    mViewModel.scoring()
                }, {
                    showProgress()
                }, { s, d ->
                    getAnimationBound().pauseAnimation()
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show()
                })
            })

        mViewModel.scoringData.observe(this, Observer {
            LiveDataUtil.useData(it, {
                analy_score.text = it?.attributes?.beauty?.male_score?.toString()
                analy_sex.text = it?.attributes?.gender?.value
                analy_age.text = it?.attributes?.age?.value.toString()
                mViewModel.similar()
            }, error = { s, face ->
                getAnimationBound().pauseAnimation()
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show()
            })
        })

        mViewModel.similarStarData.observe(this, Observer {
            LiveDataUtil.useData(it, {
                Log.i("明星", it?.faceUrl)
                // TODO: 这个URL有问题
                val glideUrl = GlideUrl(
                    it?.faceUrl, LazyHeaders.Builder().addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36"
                    ).addHeader(
                        "Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"
                    ).addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                        .addHeader("Cache-Control", "max-age=0")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Host", "img.xmqmnet.com")
                        .addHeader("Upgrade-Insecure-Requests", "1")
                        .build()
                )
                Glide.with(this).load(glideUrl).into(analy_similar_star_image)
                analy_similar_star_name.text = it?.faceOwnerName
            })
        })

        mViewModel.similarUserData.observe(this, Observer {
            LiveDataUtil.useData(it, {
                Glide.with(this).load(it?.faceUrl).into(analy_similar_user_image)
                analy_similar_user_name.text = it?.faceOwnerName
                getAnimationBound().pauseAnimation()
            }, error = { s, d ->
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
                getAnimationBound().pauseAnimation()
            })
        })
    }

    private fun showProgress() {
        getAnimationBound().setPadding(80f, 80f, 540f, 540f)
        getAnimationBound().isLoop = true
        getAnimationBound().playAnimation()
    }
}