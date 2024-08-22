package com.example.project
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.semantics.Role

object PreferencesManager {
    private const val PREFERENCES_FILE_KEY = "com.example.project.PREFERENCE_FILE_KEY"
    private const val USER_ID_KEY = "USER_ID"
    private const val TOKEN_KEY = "TOKEN_KEY"
    private const val ROLE_KEY = "ROLE_KEY"

    fun saveUserIdToPreferences(context: Context, userId: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(USER_ID_KEY, userId)
        editor.apply()
    }

    fun getUserIdFromPreferences(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID_KEY, null)
    }
    fun saveRoleToPreferences(context: Context, role: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(ROLE_KEY, role)
        editor.apply()
    }
    fun getUserRoleFromPreferences(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(ROLE_KEY, null)
    }
    fun saveTokenToPreferences(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }
    fun getTokenFromPreferences(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun clearAll(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}


