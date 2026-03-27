package com.fontakip.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ColorScheme
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
    onPrimaryContainer = Color(0xFF000000), // BinanceTextPrimary
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

private val BinanceLightColorScheme = lightColorScheme(
    primary = Color(0xFF0288D1), // BinanceYellow
    onPrimary = Color(0xFF000000), // BinanceBlack
    primaryContainer = Color(0xFF2089CC), // BinanceYellowLight
    onPrimaryContainer = Color(0xFFECC17B), // BinanceBlack Ana sayfa kucuk kutular
    secondary = Color(0xFF0097A7), // BinanceGold
    onSecondary = Color(0xFF000000), // BinanceBlack
    tertiary = Color(0xFF0288D1), // BinanceYellowLight
    onTertiary = Color(0xFF000000), // BinanceBlack
    background = Color(0xFFFFFFFF), // White
    onBackground = Color(0xFF1E1E1E), // Dark Text
    surface = Color(0xFFECE6E6), // Ana sayfa kutu renkleri
    onSurface = Color(0xFF1E1E1E), // Dark Text
    surfaceVariant = Color(0xFFEAE7E7), // Fon detay kutuları
    onSurfaceVariant = Color(0xFF3A88D9), // Gray Text
    outline = Color(0xFFFFF0F0), // Light Border
    error = Color(0xFFFF4D4D), // BinanceLossRed
    onError = Color(0xFFFFFFFF) // White
)

// ============================================
// THEME FUNCTIONS
// ============================================

// Composition Local for theme colors
val LocalAppTheme = compositionLocalOf { AppTheme.BINANCE_DARK }

/**
 * Tema adına göre ColorScheme döndürür
 */
fun getColorScheme(theme: AppTheme) = when (theme) {
    AppTheme.BINANCE_DARK -> BinanceDarkColorScheme
    AppTheme.BINANCE_LIGHT -> BinanceLightColorScheme
    AppTheme.PREMIUM_DARK -> BinanceDarkColorScheme
}

/**
 * Tema arka plan rengini döndürür
 */
fun getBackgroundColor(theme: AppTheme) = getThemeColors(theme).background

/**
 * Tema kart arka plan rengini döndürür
 */
fun getCardBackgroundColor(theme: AppTheme) = getThemeColors(theme).cardBackground

/**
 * Kâr (pozitif) rengini döndürür - Modern 2026 Vibrancy
 */
fun getProfitGreenColor(theme: AppTheme) = getThemeColors(theme).profitGreen

/**
 * Zarar (negatif) rengini döndürür - Modern 2026 Vibrancy
 */
fun getLossRedColor(theme: AppTheme) = getThemeColors(theme).lossRed

/**
 * Birincil metin rengini döndürür
 */
fun getTextPrimaryColor(theme: AppTheme) = getThemeColors(theme).textPrimary

/**
 * İkincil metin rengini döndürür
 */
fun getTextSecondaryColor(theme: AppTheme) = getThemeColors(theme).textSecondary

/**
 * Divider rengini döndürür
 */
fun getDividerColor(theme: AppTheme) = getThemeColors(theme).divider

/**
 * Birincil rengi döndürür
 */
fun getPrimaryColor(theme: AppTheme) = getThemeColors(theme).primary

/**
 * Bigbox rengini döndürür
 */
fun getBigBoxColor(theme: AppTheme) = getThemeColors(theme).bigbox

/**
 * Smallbox rengini döndürür
 */
fun getSmallBoxColor(theme: AppTheme) = getThemeColors(theme).smallbox

/**
 * Border rengini döndürür
 */
fun getBorderColor(theme: AppTheme) = getThemeColors(theme).border

/**
 * Iconics rengini döndürür
 */
fun getIconicsColor(theme: AppTheme) = getThemeColors(theme).iconics
fun getkututextColor(theme: AppTheme) = getThemeColors(theme).kututext

/**
 * Tema açık/koyu mod mu olduğunu döndürür
 */
fun isDarkTheme(theme: AppTheme) = when (theme) {
    AppTheme.BINANCE_DARK -> true
    AppTheme.BINANCE_LIGHT -> false
    AppTheme.PREMIUM_DARK -> true
}

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
 * Tema rengine göre bigbox rengini döndürür
 */
@Composable
fun getThemeBigBoxColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).bigbox
}

/**
 * Tema rengine göre smallbox rengini döndürür
 */
@Composable
fun getThemeSmallBoxColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).smallbox
}

/**
 * Tema rengine göre border rengini döndürür
 */
@Composable
fun getThemeBorderColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).border
}

/**
 * Tema rengine göre iconics rengini döndürür
 */
@Composable
fun getThemeIconicsColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).iconics
}
@Composable
fun getThemekututextColor(): Color {
    val appTheme = LocalAppTheme.current
    return getThemeColors(appTheme).kututext
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
    val typography = when (appTheme) {
        AppTheme.BINANCE_DARK -> BinanceTypography
        AppTheme.BINANCE_LIGHT -> BinanceLightTypography
        AppTheme.PREMIUM_DARK -> BinanceTypography
    }

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

// ============================================
// COLORSCHEME EXTENSIONS - ThemeColors Erişimi
// ============================================

/**
 * ColorScheme'e özel renk erişimi sağlayan extension property'ler
 * ThemeColors'daki tüm özel renklere MaterialTheme.colorScheme üzerinden erişim
 */

// Arka Plan Renkleri
val ColorScheme.themeBackground: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).background

val ColorScheme.themeCardBackground: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).cardBackground

val ColorScheme.themeCardBorder: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).cardBorder

// Vurgu Renkleri
val ColorScheme.themePrimary: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).primary

val ColorScheme.themePrimaryContainer: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).primaryContainer

val ColorScheme.themeOnPrimary: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).onPrimary

val ColorScheme.themeOnPrimaryContainer: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).onPrimaryContainer

// Status Renkleri (Kâr/Zarar)
val ColorScheme.themeProfitGreen: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).profitGreen

val ColorScheme.themeLossRed: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).lossRed

// Metin Renkleri
val ColorScheme.themeTextPrimary: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).textPrimary

val ColorScheme.themeTextSecondary: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).textSecondary

// Temel Renkler
val ColorScheme.themeWhite: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).white

val ColorScheme.themeBlack: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).black

// Surface/Material Uyumlu Renkler
val ColorScheme.themeSurface: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).surface

val ColorScheme.themeOnSurface: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).onSurface

val ColorScheme.themeSurfaceVariant: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).surfaceVariant

val ColorScheme.themeOnSurfaceVariant: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).onSurfaceVariant

val ColorScheme.themeOutline: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).outline

val ColorScheme.themeError: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).error

val ColorScheme.themeOnError: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).onError

val ColorScheme.themeDivider: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).divider

// Navigation Bar Renkleri
val ColorScheme.themeNavBarBackground: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).navBarBackground

val ColorScheme.themeNavBarIndicator: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).navBarIndicator

val ColorScheme.themeNavBarSelectedIcon: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).navBarSelectedIcon

val ColorScheme.themeNavBarSelectedText: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).navBarSelectedText

val ColorScheme.themeNavBarUnselectedIcon: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).navBarUnselectedIcon

val ColorScheme.themeNavBarUnselectedText: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).navBarUnselectedText

// Özel Kutu ve İkon Renkleri
val ColorScheme.themeBigBox: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).bigbox

val ColorScheme.themeSmallBox: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).smallbox

val ColorScheme.themeBorder: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).border

val ColorScheme.themeIconics: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).iconics

val ColorScheme.themekututext: Color
    @Composable get() = getThemeColors(LocalAppTheme.current).kututext
