package com.hustunique.coolface.pk

import com.hustunique.coolface.R
import com.hustunique.coolface.base.BaseActivity

class PkActivity : BaseActivity(R.layout.fra_pk, PkViewModel::class.java) {
    private lateinit var mViewModel: PkViewModel
    override fun init() {
        super.init()
        mViewModel = viewModel as PkViewModel
    }
}