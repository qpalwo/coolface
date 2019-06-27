package com.hustunique.coolface.util

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.hustunique.coolface.R
import com.kongzue.dialog.v2.CustomDialog

object DialogUtils {

    fun showProgressDialog(context: Context): CustomDialog {
        return CustomDialog.show(context, R.layout.dialog_progress) { c, v ->
        }
    }

    fun showTipDialog(
        context: Context,
        tip: String,
        buttonText: String,
        clickListener: (View) -> (Unit)
    ): CustomDialog {
        return CustomDialog.show(context, R.layout.dialog_onetext) { c, v ->
            v.findViewById<TextView>(R.id.dialog_one_textview).text = tip
            v.findViewById<Button>(R.id.dialog_one_button).apply {
                text = buttonText
                setOnClickListener {
                    clickListener.invoke(it)
                }
            }
        }
    }

}