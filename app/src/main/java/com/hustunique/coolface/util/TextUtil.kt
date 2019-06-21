package com.hustunique.coolface.util

import java.util.regex.Pattern

object TextUtil {
    private const val REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"

    fun isEmail(string: String): Boolean {
        return Pattern.matches(REGEX_EMAIL, string) && isNotEmpty(string)
    }

    fun isNickname(string: String): Boolean {
        return isNotEmpty(string)
    }

    fun isPassword(string: String):Boolean {
        return string.length >= 6
    }

    fun isNotEmpty(string: String): Boolean {
        return string != ""
    }
}