package com.kok.roadtoyou

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {

    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val PREF_USER_UID = "userUID"
    val PREF_USER_NAME = "userName"
    val PREF_USER_MAIL = "userMail"

    fun setUserInfo(userName: String, userMail: String, userUID: String) {
        val editor = prefs.edit()
        editor.putString(PREF_USER_NAME, userName)
        editor.putString(PREF_USER_MAIL, userMail)
        editor.putString(PREF_USER_UID, userUID)
        editor.apply()
    }

    fun getUserName(): String? {
        return prefs.getString(PREF_USER_NAME, null)
    }
    fun getUserMail(): String? {
        return prefs.getString(PREF_USER_MAIL, null)
    }
    fun getUserUID(): String? {
        return prefs.getString(PREF_USER_UID, null)
    }
}