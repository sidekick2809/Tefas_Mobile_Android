package com.fontakip.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================
// BINANCE DARK COLOR SCHEME - Modern Fintech UI
// ============================================

private val BinanceDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF0B90B), // BinanceYellow
    onPrimary = Color(0xFF000000), // BinanceBlack
    primaryContainer = Color(0xFFD4A600), // BinanceYellowDark
    onPrimaryContainer = Color(0xFFFFFFFF), // BinanceTextPrimary
    secondary = Color(0xFFFFE082), // BinanceGold
    onSecondary = Color(0xFF000000), // BinanceBlack
    tertiary = Color(0xFFFFE082), // BinanceYellowLight
    onTertiary = Color(0xFF000000), // BinanceBlack
    background = Color(0xFF000000), // BinanceBlack
    onBackground = Color(0xFFFFFFFF), // BinanceTextPrimary
    surface = Color(0xFF121212), // BinanceDarkSurface
    onSurface = Color(0xFFFFFFFF), // BinanceTextPrimary
    surfaceVariant = Color(0xFF1E1E1E), // BinanceDarkSurface2
    onSurfaceVariant = Color(0xFFB0B0B0), // BinanceTextSecondary
    outline = Color(0xFF2B2B2B), // BinanceBorder
    error = Color(0xFFFF4D4D), // BinanceLossRed
    onError = Color(0xFFFFFFFF) // BinanceTextPrimary
)

// ============================================
// THEME FUNCTIONS
// ============================================

// Composition Local for theme colors
val LocalAppTheme = compositionLocalOf { AppTheme.BINANCE_DARK }

/**
 * Tema adına göre ColorScheme döndürür
 */
fun getColorScheme(theme: AppTheme) = BinanceDarkColorScheme

/**
 * Tema arka plan rengini döndürür
 */
fun getBackgroundColor(theme: AppTheme) = BinanceBlack

/**
 * Tema kart arka plan rengini döndürür
 */
fun getCardBackgroundColor(theme: AppTheme) = Color(0xFF121212) // BinanceDarkSurface

/**
 * Kâr (pozitif) rengini döndürür - Modern 2026 Vibrancy
 */
fun getProfitGreenColor(theme: AppTheme) = Color(0xFF10C020) // BinanceProfitGreen

/**
 * Zarar (negatif) rengini döndürür - Modern 2026 Vibrancy
 */
fun getLossRedColor(theme: AppTheme) = Color(0xFFFF4D4D) // BinanceLossRed

/**
 * Birincil metin rengini döndürür
 */
fun getTextPrimaryColor(theme: AppTheme) = Color(0xFFFFFFFF) // BinanceTextPrimary

/**
 * İkincil metin rengini döndürür
 */
fun getTextSecondaryColor(theme: AppTheme) = Color(0xFFB0B0B0) // BinanceTextSecondary

/**
 * Divider rengini döndürür
 */
fun getDividerColor(theme: AppTheme) = Color(0xFF2B2B2B) // BinanceBorder

/**
 * Birincil rengi döndürür
 */
fun getPrimaryColor(theme: AppTheme) = Color(0xFFF0B90B) // BinanceYellow

/**
 * Tema açık/koyu mod mu olduğunu döndürür
 */
fun isDarkTheme(theme: AppTheme) = true

// ============================================
// GRADIENT FUNCTIONS (Legacy Support)
// ============================================

/**
 * Arka plan gradyanı (Legacy)
 */
fun getBackgroundGradient(theme: AppTheme): Brush? = null

/**
 * Kart arka planı için cam efektli gradyan (Legacy)
 */
fun getCardGradient(theme: AppTheme): Brush? = null

/**
 * Kâr (pozitif) değerleri için gradyan arka plan (Legacy)
 */
fun getProfitGradient(theme: AppTheme): Brush? = null

/**
 * Zarar (negatif) değerleri için gradyan arka plan (Legacy)
 */
fun getLossGradient(theme: AppTheme): Brush? = null

// ============================================
// COMPOSABLE THEME FUNCTIONS
// ============================================

/**
 * Tema rengine göre Primary rengi döndürür (Composable fonksiyon)
 */
@Composable
fun getPrimaryColor(): Color {
    return MaterialTheme.colorScheme.primary
}

/**
 * Tema rengine göre arka plan rengini döndürür
 */
@Composable
fun getThemeBackgroundColor(): Color {
    return MaterialTheme.colorScheme.background
}

/**
 * Tema rengine göre kart arka plan rengini döndürür
 */
@Composable
fun getThemeCardBackgroundColor(): Color {
    return MaterialTheme.colorScheme.surface
}

/**
 * Tema rengine göre hata/zarar rengini döndürür
 */
@Composable
fun getThemeErrorColor(): Color {
    return MaterialTheme.colorScheme.error
}

/**
 * Tema rengine göre birincil container rengini döndürür
 */
@Composable
fun getPrimaryContainerColor(): Color {
    return MaterialTheme.colorScheme.primaryContainer
}

/**
 * Tema rengine göre navigasyon barı arka plan rengini döndürür
 */
@Composable
fun getNavBarBackgroundColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).navBarBackground
}

/**
 * Tema rengine göre navigasyon barı indicator rengini döndürür
 */
@Composable
fun getNavBarIndicatorColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).navBarIndicator
}

/**
 * Tema rengine göre navigasyon barı seçili icon rengini döndürür
 */
@Composable
fun getNavBarSelectedIconColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).navBarSelectedIcon
}

/**
 * Tema rengine göre navigasyon barı seçili text rengini döndürür
 */
@Composable
fun getNavBarSelectedTextColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).navBarSelectedText
}

/**
 * Tema rengine göre navigasyon barı seçili olmayan icon rengini döndürür
 */
@Composable
fun getNavBarUnselectedIconColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).navBarUnselectedIcon
}

/**
 * Tema rengine göre navigasyon barı seçili olmayan text rengini döndürür
 */
@Composable
fun getNavBarUnselectedTextColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).navBarUnselectedText
}

/**
 * Tema rengine göre arka plan gradyan fırçasını döndürür
 */
@Composable
fun getBackgroundGradientBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            BinanceBlack,
            BinanceBlack
        )
    )
}

/**
 * Ana Tema Composable
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun FonTakipTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.BINANCE_DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(appTheme)
    val themeColors = getThemeColors(appTheme)
    val isDark = isDarkTheme(appTheme)
    
    // Use Binance typography
    val typography = BinanceTypography

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = themeColors.background.toArgb()
            window.navigationBarColor = themeColors.background.toArgb()
            
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
