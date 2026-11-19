@echo off
REM DersKazanimi APK Build Script
REM This script builds the APK file

setlocal enabledelayedexpansion

echo.
echo ===== DersKazanimi APK Builder =====
echo.

REM Check if gradle is installed
gradle --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Gradle bulunamadi!
    echo.
    echo Lutfen asagidaki adimlardan birini yapiniz:
    echo.
    echo Option 1: Android Studio Kur
    echo   - https://developer.android.com/studio indir
    echo   - Projeyi ac: File ^> Open ^> Bu klasor
    echo   - Build ^> Build APK(s) tikla
    echo.
    echo Option 2: Gradle Kur (Windows)
    echo   - https://gradle.org/install/ ziyaret et
    echo   - Indir ve PATH'e ekle
    echo   - Sonra bu script'i tekrar calistir
    echo.
    echo Option 3: Chocolatey ile Gradle Kur
    echo   - PowerShell'i Administrator olarak ac
    echo   - Calistir: choco install gradle
    echo   - Sonra bu script'i tekrar calistir
    echo.
    pause
    exit /b 1
)

echo [OK] Gradle bulundu!
echo.

REM Build the APK
echo [BUILDING] APK olusturuluyor... Lutfen bekleyiniz...
echo.

call gradle build -x test

if %errorlevel% equ 0 (
    echo.
    echo ===== BUILD BASARILI! =====
    echo.
    echo APK Dosyası:
    echo   %cd%\app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Bu dosyayi telefonunuza kopyalayarak kurabilirsiniz.
    echo.
    pause
) else (
    echo.
    echo [ERROR] Build basarisiz oldu!
    echo.
    pause
    exit /b 1
)

endlocal
