# Navigasyon Panel Tasarım Değişikliği Planı

## Mevcut Durum
- 5 adet navigasyon öğesi: Portföyüm, Fon Verileri, Grafik, Favoriler, Yedekle
- Material 3 NavigationBar kullanılıyor
- Mevcut indicator: CardBackground (green-ish)
- Mevcut text/icon: Black

## Hedeflenen Tasarım
- Light purple gradient arka plan + floating organic shapes
- Dark, soft-edged navigation bar panel
- Vibrant violet-purple circular active indicator
- Light-colored outline icons
- White text labels
- Sleek, glowing look for purple elements

## Yapılacak Değişiklikler

### 1. Color.kt - Yeni Renk Tanımları
```kotlin
// Light Purple Gradient Colors
val PurpleGradientStart = Color(0xFFE8D5F2)  // Light lavender
val PurpleGradientMiddle = Color(0xFFD4B8E8)  // Medium purple
val PurpleGradientEnd = Color(0xFFB794D4)    // Purple

// Violet-Purple Indicator Colors
val VioletPurple = Color(0xFF8B5CF6)         // Vibrant violet-purple
val VioletPurpleLight = Color(0xFFA78BFA)    // Light violet
val VioletPurpleDark = Color(0xFF7C3AED)     // Dark violet

// Dark Navigation Bar Colors
val NavBarDark = Color(0xFF1E1B2E)           // Dark purple-black
val NavBarDarkElevated = Color(0xFF2D2A3E)  // Slightly lighter

// Glow Effect Colors
val GlowPurple = Color(0x808B5CF6)           // 50% opacity violet
val GlowPurpleLight = Color(0x40A78BFA)     // 25% opacity light violet
```

### 2. ThemeColors.kt - Tema Renkleri Güncelleme
- Navigation bar container rengini dark purple olarak güncelle
- Indicator rengini VioletPurple olarak değiştir
- Text/icon renklerini white olarak ayarla

### 3. FonTakipNavigation.kt - Bileşen Güncellemeleri
- NavigationBar containerColor → NavBarDark
- NavigationBarItem colors:
  - selectedIconColor → White
  - selectedTextColor → White  
  - unselectedIconColor → White.copy(alpha = 0.6f)
  - unselectedTextColor → White.copy(alpha = 0.6f)
  - indicatorColor → VioletPurple (daire şeklinde)
- Icon'ları outline versiyonlarına çevir (zaten outline kullanılıyor)
- Labels → FontWeight.Medium, White

### 4. Arka Plan Gradyanı (Opsiyonel)
- Screen arka planına light purple gradient ekle
- Floating organic shapes (Blob/Shapes composable)

## Öncelik Sırası
1. [ ] Color.kt - Yeni renkleri ekle
2. [ ] ThemeColors.kt - Tema renklerini güncelle  
3. [ ] FonTakipNavigation.kt - Navigation bar tasarımını güncelle
4. [ ] Arka plan gradient ekleme (opsiyonel)

## Notlar
- Mevcut 5 sekme korunacak
- Icon'lar mevcut Material Icons kullanılacak (outline versiyonları zaten mevcut)
- Text label'lar "Portföyüm", "Fon Verileri", "Grafik", "Favoriler", "Yedekle" olarak kalacak
- Violet-purple indicator dairesel olacak ve aktif sekmede görünecek
