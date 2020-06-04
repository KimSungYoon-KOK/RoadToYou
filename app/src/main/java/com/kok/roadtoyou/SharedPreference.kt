package com.kok.roadtoyou

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {

    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val PREF_LOGIN_FLAG = "login_FLAG"
    val PREF_USER_ID = "userID"
    val PREF_USER_ID_NUM = "userID_NUM"
    val PREF_USER_NAME = "userName"
    val PREF_USER_MAIL = "userMail"

    fun setUserID(userID: String, userID_NUM: Int, login_FLAG: Boolean) {
        val editor = prefs.edit()
        editor.putString(PREF_USER_ID, userID)
        editor.putInt(PREF_USER_ID_NUM, userID_NUM)
        editor.putBoolean(PREF_LOGIN_FLAG, login_FLAG)
        editor.apply()
    }

    fun setUserInfo(userName: String, userMail: String) {
        val editor = prefs.edit()
        editor.putString(PREF_USER_NAME, userName)
        editor.putString(PREF_USER_MAIL, userMail)
        editor.apply()
    }

    fun getUserID(): String? {
        return prefs.getString(PREF_USER_ID, null)
    }

    fun getAutoLoginFlag(): Boolean {
        return prefs.getBoolean(PREF_LOGIN_FLAG, false)
    }
}