# Tefas Mobile Android

Türkiye'deki yatırım fonlarını (BES) takip etmek için geliştirilmiş modern bir Android uygulaması.

## Özellikler

### 📊 Fon Takibi
- TEFAS üzerindeki tüm yatırım fonlarının güncel fiyatlarını görüntüleme
- Fonların performans geçmişini inceleme
- Favori fonlarınızı kaydetme ve takip etme

### 💼 Portföy Yönetimi
- Kendi yatırım portföyünüzü oluşturma
- Alım-satım işlemlerini kaydetme

### 🔍 Gelişmiş Arama
- Fon koduna göre arama
- Fon adına göre arama

### 📱 Modern UI/UX
- Material Design 3 tasarım
- Binance teması
- Yumuşak geçişler ve animasyonlar

## Teknolojiler

- **Programlama Dili:** Kotlin
- **UI Framework:** Jetpack Compose
- **Mimari:** MVVM + Clean Architecture
- **Dependency Injection:** Hilt
- **Veritabanı:** Room
- **Networking:** Retrofit + OkHttp
- **Async:** Kotlin Coroutines + Flow
- **Grafik:** MPAndroidChart

## Kurulum

1. Projeyi klonlayın:
   ```bash
   git clone https://github.com/sidekick2809/Tefas_Mobile_Android.git
   ```

2. Android Studio'da açın

3. Gradle bağımlılıklarını çözümleyin:
   ```bash
   ./gradlew build
   ```

4. Uygulamayı çalıştırın

## Proje Yapısı

```
app/src/main/java/com/fontakip/
├── data/                    # Veri katmanı
│   ├── local/              # Yerel veritabanı
│   ├── remote/             # API servisleri
│   └── repository/         # Repository implementasyonları
├── domain/                  # Domain katmanı
│   ├── model/              # Domain modelleri
│   ├── repository/         # Repository arayüzleri
│   └── usecase/           # Use case'ler
├── presentation/            # Sunum katmanı
│   ├── components/        # UI bileşenleri
│   ├── navigation/        # Navigasyon
│   ├── screens/           # Ekranlar
│   ├── theme/             # Tema
│   └── viewmodel/         # ViewModel'lar
└── di/                     # Dependency Injection
```

## API

Uygulama [TEFAS](https://www.tefas.gov.tr/) (Türkiye Elektronik Fon Alım Satım Platformu) API'sini kullanmaktadır.

## Lisans

MIT License
