package com.upb.obsia.data

import android.content.Context

object SessionManager {
    private const val PREFS_NAME = "obsia_prefs"
    private const val KEY_USER_ID = "user_id"

    fun saveUserId(context: Context, userId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserId(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_USER_ID, -1)
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}