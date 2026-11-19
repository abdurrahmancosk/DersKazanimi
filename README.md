# DersKazanimi - Ders Takibi & Kazanım Göstericisi

Matematik öğretmeni ABDURRAHMAN COŞKUN için ders saatlerine göre dinamik kazanım gösteren Android uygulaması.

## 🎯 Özellikler

- 📱 **Pano**: Gerçek zamanlı saat, şu anki sınıf ve haftalık kazanımlar
- 📅 **Ders Programı**: Haftalık program görünümü (RecyclerView)
- ⚙️ **Ayarlar**: Excel yükleme, bildirim ayarları
- 📊 **Veri Yönetimi**: Excel import, Room Database
- 🔔 **Otomatik Bildirim**: WorkManager ile ders saatinden 150-40 dakika öncesi
- 🎨 **Material 3 Tasarım**: Modern ve kullanıcı dostu UI

## 🛠️ Teknoloji Stack

- **Kotlin** + Coroutines
- **Android**: Min SDK 24, Target API 34
- **Room Database**: Veri saklama
- **WorkManager**: Arka plan alarm
- **Apache POI**: Excel okuma
- **Material 3**: UI/UX
- **Fragment + ViewModel + LiveData**: Mimari

## 📊 Veri Modeli

- **TimeSlot**: Ders saatleri (11 slot, 08:20-17:00)
- **ScheduleEntry**: Haftalık program (gün, saat, sınıf)
- **WeeklyPlan**: Haftalar (başlama-bitiş tarihi)
- **LearningOutcome**: Kazanımlar (sınıf, konu, metin)

## 🚀 Hızlı Başlangıç

### APK Oluştur (GitHub Actions)

```bash
# 1. Proje klasöründe PowerShell açın
cd "C:\Users\abdur\AndroidStudioProjects\DersKazanimi"

# 2. Git repository oluştur
git init
git add .
git commit -m "Initial commit"

# 3. GitHub'da yeni repository oluştur (https://github.com/new)

# 4. GitHub'a push et
git remote add origin https://github.com/KULLANICI_ADIM/DersKazanimi.git
git branch -M main
git push -u origin main

# 5. GitHub Actions otomatik APK yapacak
# Actions sekmesinden işlemi izleyin
```

### APK İndir

1. GitHub repository'ye git
2. **Actions** sekmesine tıkla
3. Son build'i seç
4. **Artifacts** kısmından `apk` indir
5. İçinde `app-debug.apk` dosyası var

## 📱 Telefona Kurma

1. APK dosyasını telefonunuza gönderin
2. Dosya Yöneticisini açın
3. APK dosyasına tıklayın
4. "Kur" tuşuna basın
5. "Aç" tuşuna basın

## 📂 Proje Yapısı

```
DersKazanimi/
├── app/
│   ├── src/main/kotlin/com/example/derskazanimi/
│   │   ├── data/          (Entity, DAO, Database, Repository)
│   │   ├── ui/            (Fragments, Adapters)
│   │   ├── utils/         (Utilities)
│   │   ├── worker/        (WorkManager)
│   │   └── MainActivity.kt
│   ├── src/main/res/      (Layouts, Resources)
│   └── build.gradle.kts
├── .github/workflows/     (GitHub Actions)
├── build.gradle.kts       (Root)
└── settings.gradle.kts
```

## 👨‍💻 Geliştirici

Projeyi yapı taşlarına göre geliştir:

1. **Veri Yönetimi**: `data/` klasöründe Entity, DAO, Repository
2. **UI**: `ui/` klasöründe Fragment ve Adapter
3. **Arka Plan**: `worker/` klasöründe WorkManager
4. **Utilities**: `utils/` klasöründe yardımcı sınıflar

## 📝 Lisans

Açık kaynak

## 🤝 Katkıda Bulunma

Pull request'ler hoş geldiniz!

---

**Son Güncelleme:** 2026-11-19  
**Versiyon:** 1.0
