package com.hustunique.coolface.util

import android.graphics.Typeface
import android.widget.TextView
import java.util.regex.Pattern

object TextUtil {
    private const val FONT_PATH = "default_font.ttf"

    private const val REGEX_EMAIL =
        "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"

    fun isEmail(string: String): Boolean {
        return Pattern.matches(REGEX_EMAIL, string) && isNotEmpty(string)
    }

    fun isNickname(string: String): Boolean {
        return isNotEmpty(string)
    }

    fun isPassword(string: String): Boolean {
        return string.length >= 6
    }

    fun isNotEmpty(string: String): Boolean {
        return string != ""
    }

    fun setTypeface(typeface: Typeface, vararg textviews: TextView) {
        textviews.forEach {
            it.typeface = typeface
        }
    }

    fun setDefaultTypeface(vararg textViews: TextView) {
        textViews.forEach {
            setTypeface(Typeface.createFromAsset(textViews[0].context.assets, FONT_PATH), it)
        }
    }
}