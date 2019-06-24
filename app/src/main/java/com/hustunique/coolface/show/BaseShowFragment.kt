package com.hustunique.coolface.show

import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.base.BaseFragment
import com.hustunique.coolface.view.DragCardView

abstract class BaseShowFragment(@LayoutRes layoutRes: Int, viewModelClass: Class<out ViewModel>? = null) :
    BaseFragment(layoutRes, viewModelClass) {
    lateinit var dragCardView: DragCardView

    fun setDragCard(dragCardView: DragCardView) {
        this.dragCardView = dragCardView
    }

    fun getOuterActivity(): BaseShowCard {
        return activity as BaseShowCard
    }
}