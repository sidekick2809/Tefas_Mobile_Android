package com.fontakip.presentation.theme

import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    BINANCE_DARK("Binance Dark"),
    BINANCE_LIGHT("Binance Light"),
    PREMIUM_DARK("Premium Dark")
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
        // ==================== PREMIUM DARK THEME ====================
        AppTheme.PREMIUM_DARK -> ThemeColors(
            background = Color(0xFF0A0E14), // Deep Navy Black
            cardBackground = Color(0xFF151A21), // Dark Card Surface
            cardBorder = Color(0xFF2A3441), // Subtle border
            primary = Color(0xFF00D4AA), // Premium Teal/Cyan
            primaryContainer = Color(0xFF008B72), // Darker Teal
            onPrimary = Color(0xFF0A0E14), // Dark background
            onPrimaryContainer = Color(0xFFE0FFFA), // Light teal text
            profitGreen = Color(0xFF00E096), // Bright Green
            lossRed = Color(0xFFFF6B6B), // Soft Red
            textPrimary = Color(0xFFFFFFFF), // Pure White
            textSecondary = Color(0xFF8B9AAF), // Muted blue-gray
            divider = Color(0xFF2A3441), // Border color
            white = Color(0xFFFFFFFF), // White
            black = Color(0xFF000000), // Black
            surface = Color(0xFF151A21), // Surface
            onSurface = Color(0xFFFFFFFF), // On surface
            surfaceVariant = Color(0xFF1E2430), // Surface variant
            onSurfaceVariant = Color(0xFF8B9AAF), // On surface variant
            outline = Color(0xFF2A3441), // Outline
            error = Color(0xFFFF6B6B), // Error red
            onError = Color(0xFFFFFFFF), // On error
            // Navigation Bar Renkleri - Premium Style
            navBarBackground = Color(0xFF0F1419), // Dark nav bar
            navBarIndicator = Color(0xFF00D4AA), // Teal indicator
            navBarSelectedIcon = Color(0xFF00D4AA), // Teal selected
            navBarSelectedText = Color(0xFF00D4AA), // Teal selected
            navBarUnselectedIcon = Color(0xFF5A6A7A), // Muted icon
            navBarUnselectedText = Color(0xFF5A6A7A)  // Muted text
        )
        // ==================== BINANCE DARK THEME ====================
        AppTheme.BINANCE_DARK -> ThemeColors(
            background = Color(0xFF000000), // BinanceBlack
            cardBackground = Color(0xFF121212), // BinanceDarkSurface
            cardBorder = Color(0xFF2B2B2B), // BinanceBorder
            primary = Color(0xFFF0B90B), // BinanceYellow
            primaryContainer = Color(0xFFD4A600), // BinanceYellowDark
            onPrimary = Color(0xFF000000), // BinanceBlack
            onPrimaryContainer = Color(0xFF000000), // BinanceTextPrimary
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
        // ==================== BINANCE LIGHT THEME ====================
        AppTheme.BINANCE_LIGHT -> ThemeColors(
            background = Color(0xFFFFFFFF), // Çok açık buz mavisi/beyaz (Arka plan)
            cardBackground = Color(0xFFFFFFFF), // Saf beyaz kartlar
            cardBorder = Color(0xFF1366D3), // Yumuşak gri-mavi kenarlık
            primary = Color(0xFF0D9488), // Ana renk: Teal/Zümrüt yeşili
            primaryContainer = Color(0xFFCCFBF1), // Açık yeşil vurgu alanı
            onPrimary = Color(0xFFFFFFFF), 
            onPrimaryContainer = Color(0xFF134E4A),
            profitGreen = Color(0xFF10B981), // Finansal kar yeşili
            lossRed = Color(0xFFEF4444), // Finansal zarar kırmızısı
            textPrimary = Color(0xFF1E293B), // Koyu lacivert-gri metin (Okunabilirlik için)
            textSecondary = Color(0xFF64748B), // Orta ton mavi-gri alt metin
            divider = Color(0xFFF1F5F9), // Çok ince ayırıcı çizgi
            white = Color(0xFFFFFFFF),
            black = Color(0xFF000000),
            surface = Color(0xFFFFFFFF), // Hafif gri-mavi yüzey
            onSurface = Color(0xFF1E293B),
            surfaceVariant = Color(0xFFF8FAFC),
            onSurfaceVariant = Color(0xFFF8F8F8),
            outline = Color(0xFFCBD5E1),
            error = Color(0xFFEF4444),
            onError = Color(0xFFFFFFFF),
            // Navigation Bar Renkleri
            navBarBackground = Color(0xFFFFFFFF), // Beyaz alt bar
            navBarIndicator = Color(0xFFE2E8F0), // Seçili öğe arkasındaki hafif oval gölge
            navBarSelectedIcon = Color(0xFF0D9488), // Seçili ikon yeşili
            navBarSelectedText = Color(0xFF0D9488), // Seçili yazı yeşili
            navBarUnselectedIcon = Color(0xFF93A2B7), // Pasif ikonlar
            navBarUnselectedText = Color(0xFF94A3B8)  // Pasif yazılar
        )
    }
}
