# DersKazanimi - APK Derleme Rehberi

## ⚡ EN HIZLI YONTEM (Önerilen)

### PowerShell Script ile Kurulum & Derleme (Otomatik)

1. **PowerShell'i Administrator olarak açın:**
   - Windows Başlat menüsü → PowerShell (Admin)
   
2. **Script'i çalıştırın:**
   ```powershell
   cd "C:\Users\abdur\AndroidStudioProjects\DersKazanimi"
   Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
   .\INSTALL_AND_BUILD.ps1
   ```

3. **Bekleyin (5-10 dakika):**
   - Script otomatik olarak:
     - Chocolatey yükleyecek
     - Gradle yükleyecek
     - APK derleyecek

4. **APK'yı bulun:**
   ```
   C:\Users\abdur\AndroidStudioProjects\DersKazanimi\app\build\outputs\apk\debug\app-debug.apk
   ```

---

## 🔧 ALTERNATIF YÖNTEMLER

### Yöntem 2: Manual Build Script

```batch
cd C:\Users\abdur\AndroidStudioProjects\DersKazanimi
BUILD_APK.bat
```

(Öncesinde Gradle kurmış olmanız gerekiyor)

---

### Yöntem 3: Android Studio Kullanma

1. **Android Studio'yu indirin:** https://developer.android.com/studio
2. **Kurun ve açın**
3. **Projeyi açın:**
   - File → Open → `C:\Users\abdur\AndroidStudioProjects\DersKazanimi`
4. **Build yapın:**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
5. **APK'yı bulun:**
   - Bildirim → Locate → folder'ı aç

---

### Yöntem 4: Manual Gradle Install

```powershell
# Chocolatey yükle (PowerShell Admin)
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Gradle yükle
choco install gradle

# APK derle
cd C:\Users\abdur\AndroidStudioProjects\DersKazanimi
gradle build -x test
```

---

## 📱 APK Kurulum

APK dosyasını aldıktan sonra:

1. **Dosyayı telefona kopyala** (USB kablosu veya bulut)
2. **Telefonda Dosya Yöneticisini aç**
3. **APK dosyasına tıkla**
4. **"Kur" butonuna basıldığında izin ver**

---

## ✅ YAYGIN SORUNLAR

### "Gradle bulunamadı"
→ INSTALL_AND_BUILD.ps1 script'ini çalıştırın

### "Build başarısız"
→ PowerShell'i Administrator olarak açıp tekrar deneyin

### "APK dosyası oluşmadı"
→ Çıktıda hata mesajları var mı? Tekrar deneyin

---

## 📞 DESTEK

Sorunlar için:
1. Build çıktısını oku
2. Gradle'ı güncelle: `gradle --refresh-dependencies`
3. Android Studio yükle ve dene

---

**Oluşturma Tarihi:** 2026-11-19  
**Proje:** DersKazanimi v1.0
