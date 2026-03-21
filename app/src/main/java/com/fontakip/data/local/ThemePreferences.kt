package com.fontakip.data.local

import android.content.Context
import android.content.SharedPreferences
import com.fontakip.presentation.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemePreferences private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "theme_prefs",
        Context.MODE_PRIVATE
    )
    
    private val _themeFlow = MutableStateFlow(getTheme())
    val themeFlow: Flow<AppTheme> = _themeFlow.asStateFlow()

    companion object {
        private const val KEY_SELECTED_THEME = "selected_theme"
        @Volatile
        private var INSTANCE: ThemePreferences? = null
        
        fun getInstance(context: Context): ThemePreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemePreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveTheme(theme: AppTheme) {
        prefs.edit().putString(KEY_SELECTED_THEME, theme.name).apply()
        _themeFlow.value = theme
    }

    fun getTheme(): AppTheme {
        val themeName = prefs.getString(KEY_SELECTED_THEME, AppTheme.BINANCE_DARK.name)
        return try {
            AppTheme.valueOf(themeName ?: AppTheme.BINANCE_DARK.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.BINANCE_DARK
        }
    }
}
