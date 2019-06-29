package com.hustunique.coolface.util

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.hustunique.coolface.R
import com.kongzue.dialog.v2.CustomDialog

object DialogUtil {

    fun showProgressDialog(context: Context, message: String = ""): CustomDialog {
        return CustomDialog.show(context, R.layout.dialog_progress) { c, v ->
            if (!message.isEmpty()) {
                v.findViewById<TextView>(R.id.dialog_progress_message).apply {
                    visibility = View.VISIBLE
                    text = message
                }
            } else {
                v.findViewById<TextView>(R.id.dialog_progress_message).visibility = View.GONE
            }
        }
    }

    fun showTipDialog(
        context: Context,
        tip: String,
        firstButtonText: String,
        firstClickListener: (CustomDialog) -> (Unit) = {},
        secondButtonText: String = "",
        secondClickListener: (CustomDialog) -> (Unit) = {}
    ): CustomDialog {
        return CustomDialog.show(context, R.layout.dialog_onetext) { c, v ->
            v.findViewById<TextView>(R.id.dialog_one_textview).text = tip
            v.findViewById<Button>(R.id.dialog_first_button).apply {
                text = firstButtonText
                setOnClickListener {
                    firstClickListener.invoke(c)
                }
            }
            if (secondButtonText.isNotEmpty()) {
                v.findViewById<Button>(R.id.dialog_second_button).apply {
                    visibility = View.VISIBLE
                    text = secondButtonText
                    setOnClickListener {
                        secondClickListener.invoke(c)
                    }
                }
            }
        }
    }

}