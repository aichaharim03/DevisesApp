package com.attijariwafabank.devisesapp.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {
    fun saveTheme(context: Context, nightMode: Int) {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        val sharedPref = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
        sharedPref.edit().putInt("night_mode", nightMode).apply()
    }

    fun loadTheme(context: Context): Int {
        val sharedPref = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
        return sharedPref.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO) // default: light
    }
}

