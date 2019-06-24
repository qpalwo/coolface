package com.hustunique.coolface.main.navigation

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.hustunique.coolface.R
import com.hustunique.coolface.bean.User
import com.hustunique.coolface.show.BaseShowFragment
import com.hustunique.coolface.view.DragCardView
import kotlinx.android.synthetic.main.card_nickname.*

class NicknameCardFragment: BaseShowFragment(R.layout.card_nickname) {

    override fun initContact(context: Context?) {
        super.initContact(context)
        btn_main_nickname.setOnClickListener {
            val user = BmobUser.getCurrentUser(User::class.java)
            val newName = et_main_nickname.text.toString()
            user.nickname = newName
            user.update(object : UpdateListener() {
                override fun done(p0: BmobException?) {
                    val toastText = if (p0 == null)  "修改昵称成功" else "修改昵称失败"
                    Toast.makeText(activity?.applicationContext, toastText, Toast.LENGTH_SHORT).show()
                    activity?.supportFinishAfterTransition()
                }
            })
        }
        dragCardView.setFinishCallback(object : DragCardView.FinishCallback {
            override fun onGoAway() {
                activity?.finish()
            }
        })
    }

}