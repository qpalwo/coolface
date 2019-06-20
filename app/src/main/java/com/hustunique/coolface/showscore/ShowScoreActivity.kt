package com.hustunique.coolface.showscore

import com.bumptech.glide.Glide
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
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
        Glide.with(this).load(mViewModel.getPictureFile()).into(show_score_imgview)
        mViewModel.scoring()
    }

    override fun initContact() {
        super.initContact()

    }

}
