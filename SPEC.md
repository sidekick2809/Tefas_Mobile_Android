# FonTakip - Technical Specification Document

## 1. Project Overview

### Project Information
- **Project Name**: FonTakip (Fund Tracker)
- **Project Type**: Native Android Mobile Application
- **Core Functionality**: Personal investment portfolio tracker for Turkish mutual funds and stocks with portfolio management, profit/loss tracking, and daily performance monitoring.

### Technology Stack
- **Language**: Kotlin 1.9.x
- **UI Framework**: Jetpack Compose with Material Design 3
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture Pattern**: Clean Architecture (MVVM)
- **Dependency Injection**: Hilt
- **Local Database**: Room
- **Async Operations**: Kotlin Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose

---

## 2. UI/UX Specification

### Color Palette

| Color Name | Hex Code | Usage |
|------------|----------|-------|
| Primary Blue | #1565C0 | Top navigation bar, active tabs |
| Primary Blue Dark | #0D47A1 | Status bar, pressed states |
| Primary Blue Light | #42A5F5 | Highlights, secondary elements |
| Background | #F5F5F5 | Screen background |
| Card White | #FFFFFF | Card backgrounds |
| Text Primary | #212121 | Main text content |
| Text Secondary | #757575 | Secondary/labels text |
| Profit Green | #4CAF50 | Positive values, profit |
| Profit Green Light | #E8F5E9 | Positive value backgrounds |
| Loss Red | #F44336 | Negative values, loss |
| Loss Red Light | #FFEBEE | Negative value backgrounds |
| Divider | #E0E0E0 | Borders and dividers |

### Typography

| Style | Font | Size | Weight | Usage |
|-------|------|------|--------|-------|
| Title Large | System Default | 24sp | Bold | Portfolio value display |
| Title Medium | System Default | 20sp | SemiBold | Screen titles |
| Title Small | System Default | 16sp | SemiBold | Card headers |
| Body Large | System Default | 16sp | Regular | Main content |
| Body Medium | System Default | 14sp | Regular | Secondary content |
| Body Small | System Default | 12sp | Regular | Labels, timestamps |
| Caption | System Default | 10sp | Regular | Small metadata |

### Spacing System (8pt Grid)

- **xs**: 4dp
- **sm**: 8dp
- **md**: 16dp
- **lg**: 24dp
- **xl**: 32dp
- **xxl**: 48dp

### Screen Structure

```
App
├── Splash Screen
├── Login Screen (PIN)
└── Main Screen (with Bottom Navigation)
    ├── Portfolio Tab (Main Portfolio)
    │   ├── Top Navigation Bar
    │   ├── Portfolio Summary Card
    │   ├── Fund/Stock Cards List
    │   └── Bottom Navigation Bar
    ├── Analytics Tab
    ├── Search Tab
    ├── Favorites Tab
    └── Profile Tab
```

### Component Specifications

#### 1. Top Navigation Bar
- **Height**: 56dp
- **Background**: Primary Blue (#1565C0)
- **Left Section**: Arrow icons (< and >) - 24dp, white
- **Center**: Title "Ana Portföy" - 18sp, white, bold
- **Right Section**: 
  - Logout icon (24dp, white)
  - Settings gear icon (24dp, white)
  - More options icon (24dp, white)
- **Content Area Add Button**: "+" icon (24dp, blue) in top-right of content

#### 2. Portfolio Summary Card
- **Background**: White (#FFFFFF)
- **Corner Radius**: 12dp
- **Elevation**: 4dp
- **Padding**: 16dp
- **Margin**: 16dp horizontal, 8dp vertical
- **Content Structure**:
  ```
  ┌─────────────────────────────────────┐
  │ Portfolio Value                     │
  │ 17.532,51 TL                        │
  │─────────────────────────────────────│
  │  Alış Maliyeti  │  Kar/Zarar (TL)  │
  │  17.326,13 TL   │  206,38 TL (G)   │
  │─────────────────────────────────────│
  │           Kar/Zarar (%)             │
  │            1,191% (G)               │
  │─────────────────────────────────────│
  │ Portföy (Günlük): 1,755% / 307,71 TL│
  │        [✓]              [+]        │
  │─────────────────────────────────────│
  │ Son Güncelleme: 19-05-2022 09:52:48 │
  └─────────────────────────────────────┘
  ```

#### 3. Fund/Stock Card
- **Background**: White (#FFFFFF)
- **Corner Radius**: 12dp
- **Elevation**: 2dp
- **Margin**: 16dp horizontal, 8dp vertical
- **Padding**: 16dp
- **Structure**:
  ```
  ┌─────────────────────────────────────────┐
  │ IPJ - 11019,15 TL (%62,850)      [ℹ][✏️]│
  │ İŞ PORTFÖY ELEKTRİKLİ ARAÇLAR KARMA... │
  │─────────────────────────────────────────│
  │ Yatırım Miktarı │ Yatırım Tarihi │ Sür │
  │ 10.546,15 TL    │ 26/04/2022     │ 23.G│
  │─────────────────────────────────────────│
  │ Günlük Değişim │ Kazanç/Zarar  │ Gün.Ö│
  │ 2,76%(G)       │ 4,485%(G)     │ 0,195│
  │─────────────────────────────────────────│
  │                        Veri: 18/05/2022│
  └─────────────────────────────────────────┘
  ```

#### 4. Bottom Navigation Bar
- **Height**: 56dp + safe area
- **Background**: White (#FFFFFF)
- **Elevation**: 8dp
- **5 Tabs**:
  1. Portföyüm (pie chart icon) - Active: Blue, Inactive: Gray
  2. Analiz (chart icon) - Gray
  3. Keşfet (search icon) - Gray
  4. Favoriler (star icon) - Gray
  5. Profil (person icon) - Gray
- **Label**: 12sp, below icon
- **Active Indicator**: Blue underline or highlight

---

## 3. Data Models

### Portfolio
```kotlin
data class Portfolio(
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Fund/Stock Asset
```kotlin
data class Asset(
    val id: Long = 0,
    val portfolioId: Long,
    val code: String,           // Fund code e.g., "IPJ"
    val name: String,           // Full fund name
    val type: AssetType,        // MUTUAL_FUND, STOCK
    val units: Double,          // Number of units owned
    val purchasePrice: Double, // Price per unit at purchase
    val purchaseDate: Long,    // Purchase timestamp
    val currentPrice: Double = 0.0,
    val lastUpdateDate: Long = 0
)

enum class AssetType {
    MUTUAL_FUND,
    STOCK
}
```

### Portfolio Summary
```kotlin
data class PortfolioSummary(
    val totalValue: Double,
    val totalCost: Double,
    val profitLossTL: Double,
    val profitLossPercent: Double,
    val dailyChangeTL: Double,
    val dailyChangePercent: Double
)
```

### Asset Performance
```kotlin
data class AssetPerformance(
    val assetId: Long,
    val dailyChangePercent: Double,
    val dailyChangeTL: Double,
    val totalGainLossPercent: Double,
    val totalGainLossTL: Double,
    val dailyAverageChangePercent: Double,
    val dailyAverageChangeTL: Double,
    val investmentDays: Int
)
```

---

## 4. Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────────┐
│              Presentation Layer            │
│  (Compose UI, ViewModels, UI State)         │
├─────────────────────────────────────────────┤
│               Domain Layer                  │
│  (Use Cases, Domain Models, Interfaces)     │
├─────────────────────────────────────────────┤
│                Data Layer                   │
│  (Repositories, Room DB, API Services)      │
└─────────────────────────────────────────────┘
```

### Package Structure
```
com.fontakip
├── data
│   ├── local
│   │   ├── database
│   │   │   ├── FontakipDatabase
│   │   │   ├── dao
│   │   │   └── entities
│   │   └── preferences
│   ├── repository
│   └── model
├── domain
│   ├── model
│   ├── repository
│   └── usecase
├── presentation
│   ├── components
│   ├── navigation
│   ├── screens
│   │   ├── auth
│   │   ├── portfolio
│   │   ├── analytics
│   │   ├── search
│   │   ├── favorites
│   │   └── profile
│   ├── theme
│   └── viewmodel
├── di
└── FontakipApplication
```

---

## 5. Functionality Specification

### Core Features

1. **Portfolio Management**
   - View portfolio summary with total value
   - View individual fund/stock holdings
   - Calculate profit/loss in TL and percentage
   - Calculate daily changes
   - Support multiple portfolios (switch with arrows)

2. **Asset Management**
   - Add new fund/stock to portfolio
   - Edit existing asset details
   - Delete asset from portfolio
   - View asset information

3. **Data Display**
   - Color-coded profit (green) and loss (red)
   - Investment duration tracking
   - Last update timestamp display
   - Percentage and absolute value display

4. **Navigation**
   - Bottom navigation with 5 tabs
   - Portfolio switching with arrow buttons
   - Settings access
   - Logout functionality

5. **Authentication**
   - PIN-based local authentication
   - Session management

### State Management
- **ViewModel + StateFlow**: For reactive UI updates
- **UiState sealed class**: For representing loading, success, error states
- **Repository pattern**: For data access abstraction

---

## 6. Mock Data

### Sample Portfolio
```kotlin
val samplePortfolio = Portfolio(
    id = 1,
    name = "Ana Portföy"
)

val sampleAssets = listOf(
    Asset(
        id = 1,
        portfolioId = 1,
        code = "IPJ",
        name = "İŞ PORTFÖY ELEKTRİKLİ ARAÇLAR KARMA",
        type = AssetType.MUTUAL_FUND,
        units = 100.0,
        purchasePrice = 105.4615,
        purchaseDate = 1650931200000, // 26/04/2022
        currentPrice = 110.1915,
        lastUpdateDate = 1652822400000 // 18/05/2022
    ),
    Asset(
        id = 2,
        portfolioId = 1,
        code = "ZBB",
        name = "ZİRAAT PORTFÖY BİST 30 ENDEKSİ HİSSE",
        type = AssetType.MUTUAL_FUND,
        units = 50.0,
        purchasePrice = 135.6,
        purchaseDate = 1650844800000, // 25/04/2022
        currentPrice = 130.267,
        lastUpdateDate = 1652822400000 // 18/05/2022
    )
)
```

---

## 7. Implementation Notes

### Key Considerations
1. Use Turkish locale for number formatting (1.234,56 TL)
2. Handle empty portfolio state gracefully
3. Implement pull-to-refresh for data updates
4. Support both light theme (dark status bar on light background)
5. Implement proper error handling with user-friendly messages

### Testing Strategy
1. Unit tests for ViewModels and Use Cases
2. Instrumented tests for Room database
3. UI tests for critical flows

---

## 8. Dependencies

### Core Dependencies
- Jetpack Compose BOM 2024.02.00
- Material 3
- Room 2.6.1
- Hilt 2.50
- Navigation Compose 2.7.7
- Lifecycle ViewModel Compose 2.7.0
- Coroutines 1.7.3

### Additional Dependencies
- Accompanist (System UI Controller)
- Kotlinx Datetime

---

*Document Version: 1.0*
*Last Updated: 2026-03-08*
