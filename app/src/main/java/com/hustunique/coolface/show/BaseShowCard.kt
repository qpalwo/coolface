package com.hustunique.coolface.show

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.ChangeTransform
import android.transition.Fade
import android.transition.Slide
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import kotlinx.android.synthetic.main.base_show_card.*

class BaseShowCard : BaseActivity(R.layout.base_show_card) {
    companion object {
        private val FRA_ARGS: String = "fragment_args"
        private lateinit var baseShowFragment: BaseShowFragment
        fun start(context: Context, baseShowFragment: BaseShowFragment) {
            start(context, baseShowFragment, null, null)
        }

        fun start(context: Context, baseShowFragment: BaseShowFragment, fragmentArgs: Bundle?) {
            Companion.start(context, baseShowFragment, fragmentArgs, null)
        }

        fun start(
            context: Context,
            baseShowFragment: BaseShowFragment,
            fragmentArgs: Bundle?,
            activityBundle: Bundle?
        ) {
            this.baseShowFragment = baseShowFragment
            val intent = Intent(context, BaseShowCard::class.java)
            intent.putExtra(FRA_ARGS, fragmentArgs)
            if (activityBundle != null)
                context.startActivity(intent, activityBundle)
            else
                context.startActivity(intent)
        }
    }

    override fun init() {
        baseShowFragment.setDragCard(card_container)
        supportFragmentManager.beginTransaction().add(R.id.card_container, baseShowFragment).commit()
        baseShowFragment.arguments = intent.getBundleExtra(FRA_ARGS)
    }

    override fun initView() {
        super.initView()
    }
}