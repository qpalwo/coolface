package com.hustunique.coolface.showscore

import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.bean.Status
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
        mViewModel.pictureData.observe(this, Observer<Resource<String>> {
            when (it.status) {
                Status.SUCCESS -> {
                    Glide.with(this)
                        .load(it.data)
                        .into(show_score_imgview)
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }

        })
//        mViewModel.scoring()
    }

    override fun initContact() {
        super.initContact()

    }

}
