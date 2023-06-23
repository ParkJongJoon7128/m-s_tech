package Util

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcel
import android.os.Parcelable

class PreferenceUtil(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("prefs_name", 0)

    fun getData(key: String, value: String): String {
        return prefs.getString(key, value).toString()
    }

    fun setData(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}