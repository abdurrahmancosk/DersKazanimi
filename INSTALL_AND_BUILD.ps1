# DersKazanimi - Gradle Install & Build Script

$ErrorActionPreference = "Continue"

Write-Host "`n===== DersKazanimi Build Tool =====" -ForegroundColor Cyan
Write-Host "Gradle yukleme ve APK derleme..." -ForegroundColor Yellow

# Check if Chocolatey is installed
$chocoCheck = Get-Command choco -ErrorAction SilentlyContinue
if ($chocoCheck) {
    Write-Host "✓ Chocolatey bulundu" -ForegroundColor Green
    Write-Host "Gradle kuruluyor..." -ForegroundColor Yellow
    choco install gradle -y
} else {
    Write-Host "`n[1] Chocolatey Kurulumu" -ForegroundColor Cyan
    Write-Host "Chocolatey henuz kurulu degil. Otomatik kuruluyor...`n"

    # Install Chocolatey
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

    Write-Host "`n✓ Chocolatey kuruldu" -ForegroundColor Green
    Write-Host "`n[2] Gradle Kurulumu" -ForegroundColor Cyan
    Write-Host "Gradle kuruluyor...`n"

    choco install gradle -y
}

# Verify gradle installation
$gradleCheck = gradle --version
if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ Gradle basariyla kuruldu!`n" -ForegroundColor Green
} else {
    Write-Host "`n✗ Gradle kurulumu basarisiz!`n" -ForegroundColor Red
    exit 1
}

# Build APK
Write-Host "[3] APK Derleme" -ForegroundColor Cyan
Write-Host "APK olusturuluyor (1-5 dakika arasinda surabilir)...`n"

cd "C:\Users\abdur\AndroidStudioProjects\DersKazanimi"
gradle build -x test

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n" -ForegroundColor Green
    Write-Host "╔════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║   BUILD BASARILI!                  ║" -ForegroundColor Green
    Write-Host "╚════════════════════════════════════╝" -ForegroundColor Green

    $apkPath = "C:\Users\abdur\AndroidStudioProjects\DersKazanimi\app\build\outputs\apk\debug\app-debug.apk"

    if (Test-Path $apkPath) {
        Write-Host "`n✓ APK Dosyası Hazir:" -ForegroundColor Green
        Write-Host "   $apkPath`n" -ForegroundColor Cyan

        $apkSize = (Get-Item $apkPath).Length / 1MB
        Write-Host "   Boyut: $([math]::Round($apkSize, 2)) MB`n"

        Write-Host "KULLANIM:" -ForegroundColor Yellow
        Write-Host "1. Bu dosyayi telefonunuza kopyalayin"
        Write-Host "2. Telefondan AC > Dosya Yoneticisi > APK dosyasina tiklayin"
        Write-Host "3. 'Kur' tusuna basiniz`n"
    }
} else {
    Write-Host "`n✗ Build Hatasi!`n" -ForegroundColor Red
    Write-Host "Hata detaylari yukarida yazili.`n"
}

Write-Host "Devam etmek icin bir tusa basiniz..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
