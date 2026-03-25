# Portföy Sayfası Pull-to-Refresh Düzeltme Planı

## Sorun
Portföy sayfasında ekranı yukarıdan aşağı kaydırınca (pull-to-refresh):
- **Mevcut davranış:** TEFAS'tan yeni veri çekmeye başlıyor
- **Beklenen davranış:** Veritabanındaki mevcut fon fiyatlarını yeniden yükleyerek ekrandaki değerleri güncellemesi gerekiyor
- **Fon Verileri sayfasındaki gesture ile karışmamalı**

## Analiz
- [`MainPortfolioScreen.kt`](app/src/main/java/com/fontakip/presentation/screens/portfolio/MainPortfolioScreen.kt:148-151) - Pull-to-refresh tetiklendiğinde `viewModel.refresh()` çağrılıyor
- [`PortfolioViewModel.kt`](app/src/main/java/com/fontakip/presentation/viewmodel/PortfolioViewModel.kt:109-111) - `refresh()` metodu `loadPortfolios()` çağırıyor
- `loadAssetsForCurrentPortfolio()` metodu veritabanından fon fiyatlarını çekiyor (API değil, yerel veritabanı)

## Düzeltme Adımları

### 1. MainPortfolioScreen.kt:
- `fonViewModel` parametresini kontrol et - gerekirse kaldır
- Pull-to-refresh tetiklendiğinde sadece `viewModel.refresh()` çağrılmalı
- TEFAS API çağrısı YAPILMAMALI

### 2. PortfolioViewModel.kt:
- `refresh()` metodu veritabanından veri çekmeli (mevcut hali doğru görünüyor)
- Gerekirse optimizasyon yapılabilir

## Beklenen Sonuç
Pull-to-refresh gesture'ı:
- Fon Verileri sayfasından BAĞIMSIZ çalışacak
- Sadece veritabanındaki mevcut fon verilerini yeniden yükleyecek
- Ekrandaki değerleri güncelleyecek
- TEFAS'tan yeni veri çekmeyecek