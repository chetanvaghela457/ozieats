package com.admin.ozieats_app.utils

import android.content.Context

class SharedPrefsManager private constructor(private val context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES = "sPrefs"
        const val USER_TOKEN = "user_token"

        @Synchronized
        fun newInstance(context: Context) = SharedPrefsManager(context)
    }

    fun putBoolean(key: String, value: Boolean) = preferences.edit().putBoolean(key, value).apply()

    fun putFloat(key: String, value: Float) = preferences.edit().putFloat(key, value).apply()

    fun putInt(key: String, value: Int) = preferences.edit().putInt(key, value).apply()

    fun putLong(key: String, value: Long) = preferences.edit().putLong(key, value).apply()

    fun putString(key: String, value: String) = preferences.edit().putString(key, value).apply()

    fun putStringSet(key: String, value: Set<String>) = preferences.edit().putStringSet(key, value).apply()

    fun getBoolean(key: String, defValue: Boolean) = preferences.getBoolean(key, defValue)

    fun getFloat(key: String, defValue: Float) = preferences.getFloat(key, defValue)

    fun getInt(key: String, defValue: Int) = preferences.getInt(key, defValue)

    fun getLong(key: String, defValue: Long) = preferences.getLong(key, defValue)

    fun getString(key: String, defValue: String) = preferences.getString(key, defValue)

    fun getString(key: String) = preferences.getString(key, "")

    fun getStringSet(key: String, defValue: Set<String>) = preferences.getStringSet(key, defValue)

    fun getAllValues() = preferences.all

    fun clearData() = preferences.edit().clear().apply()

    /*fun putUser(key: String, value: String) = putString(key, value)

    fun getUser(key: String) = getString(key, "")

    fun putSupplier(key: String, value: String) = putString(key, value)

    fun getSupplier(key: String) = getString(key, "")

    fun putToken(key: String, value: String) = putString(key, value)

    fun getToken(key: String) = getString(key, "")*/

    fun putLocation(key: String, value: String) = putString(key, value)

    fun getLocation(key: String) = getString(key, "")

    fun saveAuthToken(token: String) {
        val editor = preferences.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return preferences.getString(USER_TOKEN, null)
    }

    fun putRestaurantData(restauranntData:String)
    {
        val editor=preferences.edit()
        editor.putString(Preference.RESTAURANT_DATA,restauranntData)
        editor.apply()
    }

    fun removePreference(key:String)
    {
        preferences.edit().remove(key).clear().apply()
    }



}