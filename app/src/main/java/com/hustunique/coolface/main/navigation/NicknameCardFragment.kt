package com.hustunique.coolface.main.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
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
        val user = BmobUser.getCurrentUser(User::class.java)
        et_main_nickname.setText(user.nickname)
        et_main_nickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn_main_nickname.isEnabled = et_main_nickname.text.toString().isNotBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        btn_main_nickname.setOnClickListener {
            val user = BmobUser.getCurrentUser(User::class.java)
            val newName = et_main_nickname.text.toString()
            user.nickname = newName
            user.update(object : UpdateListener() {
                override fun done(p0: BmobException?) {
                    Log.d("bmob", p0?.errorCode.toString() + p0?.message)
                    val toastText = if (p0 == null)  "修改昵称成功" else p0.message
                    Toast.makeText(activity?.applicationContext, toastText, Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra("nickname", newName)
                    activity?.setResult(Activity.RESULT_OK, intent)
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