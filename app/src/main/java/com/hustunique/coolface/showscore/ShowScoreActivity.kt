package com.hustunique.coolface.showscore

import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.util.LivaDataUtil
import kotlinx.android.synthetic.main.activity_show_score.*

class ShowScoreActivity : BaseActivity(R.layout.activity_show_score, ShowScoreViewModel::class.java) {

    private lateinit var mViewModel: ShowScoreViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as ShowScoreViewModel
        mViewModel.init(applicationContext)
    }

    override fun initView() {
        super.initView()
        mViewModel.pictureData.observe(this, Observer {
            LivaDataUtil.useData(it, {
                Glide.with(this)
                    .load(it)
                    .into(show_score_imgview)
            })
        })
        mViewModel.scoringData.observe(this, Observer {
            LivaDataUtil.useData(it, {
                score.text = it?.attributes?.beauty?.male_score?.toString() ?: "0"
            }, {
                score.text = "loading"
            }, { s, data ->
                score.text = s
            })
        })
        mViewModel.postData.observe(this, Observer {
            LivaDataUtil.useData(it, {
                message.text = it?.message ?: "0"
            }, {
                message.text = "loading"
            }, { s, data ->
                message.text = s
            })
        })
        mViewModel.similarData.observe(this, Observer {
            LivaDataUtil.useData(it, {
                similar.text = it?.faceOwnerName
            })
        })
    }

    override fun initContact() {
        super.initContact()
        scoring.setOnClickListener {
            mViewModel.scoring()
        }
        post.setOnClickListener {
            mViewModel.post("input message")
        }
        search.setOnClickListener {
            mViewModel.similar()
        }
    }

}
