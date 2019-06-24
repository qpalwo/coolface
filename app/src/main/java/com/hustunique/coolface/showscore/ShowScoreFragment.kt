package com.hustunique.coolface.showscore

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
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
        mViewModel.init(context!!)
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
            getAnimationBound().pauseAnimation()
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
                getAnimationBound().pauseAnimation()
            }, error = { s, face ->
                getAnimationBound().pauseAnimation()
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show()
            })
        })
    }

    private fun showProgress() {
        getAnimationBound().setPadding(80f, 80f, 540f, 540f)
        getAnimationBound().isLoop = true
        getAnimationBound().playAnimation()
    }
}