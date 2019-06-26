package com.hustunique.coolface.show

import android.content.Intent
import android.os.Bundle
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.util.DisplayUtil
import com.hustunique.coolface.view.DragCardView
import kotlinx.android.synthetic.main.base_show_card.*

class BaseShowCard : BaseActivity(R.layout.base_show_card) {
    companion object {
        private val FRA_ARGS: String = "fragment_args"
        private lateinit var baseShowFragment: BaseShowFragment
        fun start(
            activity: BaseActivity,
            baseShowFragment: BaseShowFragment,
            fragmentArgs: Bundle? = null,
            activityBundle: Bundle? = null,
            requestCode: Int = -1
        ) {
            this.baseShowFragment = baseShowFragment
            val intent = Intent(activity, BaseShowCard::class.java)
            intent.putExtra(FRA_ARGS, fragmentArgs)
            activity.startActivityForResult(intent, requestCode, activityBundle)
        }
    }

    override fun init() {
        baseShowFragment.setDragCard(card_container)
        card_container.setFinishCallback(object : DragCardView.FinishCallback {
            override fun onGoAway() {
                supportFinishAfterTransition()
            }
        })
        supportFragmentManager.beginTransaction().add(R.id.card_container, baseShowFragment).commit()
        baseShowFragment.arguments = intent.getBundleExtra(FRA_ARGS)
    }


    fun getAnimationBound() = card_bound
}