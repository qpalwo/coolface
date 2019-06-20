package com.hustunique.coolface.showcard

import android.widget.Toast
import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity
import com.hustunique.coolface.view.DragCardView
import kotlinx.android.synthetic.main.activity_show.*

class ShowActivity : BaseActivity(R.layout.activity_show) {
    override fun initView() {
        super.initView()
        show_card.setFinishCallback(object : DragCardView.FinishCallback {
            override fun onGoAway() {
                Toast.makeText(this@ShowActivity, "离开屏幕", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }
}