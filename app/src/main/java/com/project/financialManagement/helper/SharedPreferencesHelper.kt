package com.project.financialManagement.helper

import android.content.Context
import android.content.SharedPreferences
import com.project.financialManagement.model.User

class SharedPreferencesHelper(context: Context) {
    companion object {
        private const val PREF_NAME = "user_prefs"
        private const val KEY_NAME = "name"
        private const val KEY_COIN = "coin"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_LIMIT = "limit"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        with(sharedPreferences.edit()) {
            putString(KEY_NAME, user.name)
            apply()
        }
    }

    fun getUser(){
        val name = sharedPreferences.getString(KEY_NAME, null)
    }

    fun clearUser() {
        with(sharedPreferences.edit()) {
            remove(KEY_NAME)
            apply()
        }
    }

    fun saveCoinId(coinId: Int) {
        with(sharedPreferences.edit()) {
            putInt(KEY_COIN, coinId)
            apply()
        }
    }

    fun getCoinId(): Int{
        return sharedPreferences.getInt(KEY_COIN, 0)
    }

    fun saveLangPosition(position: Int) {
        with(sharedPreferences.edit()) {
            putInt(KEY_LANGUAGE, position)
            apply()
        }
    }

    fun getLangPosition(): Int? {
        return sharedPreferences.getInt(KEY_LANGUAGE, 0)
    }

    fun saveLimit(limit: Float) {
        with(sharedPreferences.edit()) {
            putFloat(KEY_LIMIT, limit)
            apply()
        }
    }

    fun getLimit(): Float? {
        return sharedPreferences.getFloat(KEY_LIMIT, 0f)
    }
}