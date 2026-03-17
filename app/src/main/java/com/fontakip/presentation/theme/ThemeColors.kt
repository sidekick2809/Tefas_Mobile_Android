package com.fontakip.presentation.theme

import androidx.compose.ui.graphics.Color

// ============================================
// MATERIAL DESIGN THEMES - BLUE GREY ONLY
// ============================================

/**
 * Tema Seçenekleri Enum
 * Material Design Color Palettes: Blue Grey (Light and Dark)
 * + Modern 2026 Fintech Themes
 */
enum class AppTheme(val displayName: String) {
    BINANCE_DARK("Binance Dark")
}

// Tema Renkleri Sınıfı
data class ThemeColors(
    // Arka Plan
    val background: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    
    // Vurgu Renkleri
    val primary: Color,
    val primaryContainer: Color,
    val onPrimary: Color,
    val onPrimaryContainer: Color,
    
    // Status Renkleri
    val profitGreen: Color,
    val lossRed: Color,
    
    // Metin Renkleri
    val textPrimary: Color,
    val textSecondary: Color,
    
    // Diğer
    val divider: Color,
    val white: Color,
    val black: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val error: Color,
    val onError: Color,
    
    // Navigation Bar Renkleri (Yeni Eklenen)
    val navBarBackground: Color,
    val navBarIndicator: Color,
    val navBarSelectedIcon: Color,
    val navBarSelectedText: Color,
    val navBarUnselectedIcon: Color,
    val navBarUnselectedText: Color
)

// Tema Renkleri Uygulama Fonksiyonu
fun getThemeColors(theme: AppTheme): ThemeColors {
    return when (theme) {
        // ==================== BINANCE DARK THEME ====================
        AppTheme.BINANCE_DARK -> ThemeColors(
            background = Color(0xFF000000), // BinanceBlack
            cardBackground = Color(0xFF121212), // BinanceDarkSurface
            cardBorder = Color(0xFF2B2B2B), // BinanceBorder
            primary = Color(0xFFF0B90B), // BinanceYellow
            primaryContainer = Color(0xFFD4A600), // BinanceYellowDark
            onPrimary = Color(0xFF000000), // BinanceBlack
            onPrimaryContainer = Color(0xFFFFFFFF), // BinanceTextPrimary
            profitGreen = Color(0xFF10C020), // BinanceProfitGreen
            lossRed = Color(0xFFFF4D4D), // BinanceLossRed
            textPrimary = Color(0xFFFFFFFF), // BinanceTextPrimary
            textSecondary = Color(0xFFB0B0B0), // BinanceTextSecondary
            divider = Color(0xFF2B2B2B), // BinanceBorder
            white = Color(0xFFFFFFFF), // BinanceTextPrimary
            black = Color(0xFF000000), // BinanceBlack
            surface = Color(0xFF121212), // BinanceDarkSurface
            onSurface = Color(0xFFFFFFFF), // BinanceTextPrimary
            surfaceVariant = Color(0xFF1E1E1E), // BinanceDarkSurface2
            onSurfaceVariant = Color(0xFFB0B0B0), // BinanceTextSecondary
            outline = Color(0xFF2B2B2B), // BinanceBorder
            error = Color(0xFFFF4D4D), // BinanceLossRed
            onError = Color(0xFFFFFFFF), // BinanceTextPrimary
            // Navigation Bar Renkleri - Binance Style
            navBarBackground = Color(0xFF14151C), // BinanceNavBarBackground
            navBarIndicator = Color(0xFFF0B90B), // BinanceYellow
            navBarSelectedIcon = Color(0xFFF0B90B), // BinanceYellow
            navBarSelectedText = Color(0xFFF0B90B), // BinanceYellow
            navBarUnselectedIcon = Color(0xFF707070), // BinanceNavBarInactive
            navBarUnselectedText = Color(0xFF707070) // BinanceNavBarInactive
        )
    }
}
