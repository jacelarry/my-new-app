# Build script for SMS Automation App (Windows PowerShell)
# This script mimics the CI/CD pipeline locally

$ErrorActionPreference = "Stop"

Write-Host "🚀 Starting local build process..." -ForegroundColor Cyan
Write-Host ""

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_ -replace '.*"(\d+).*', '$1' }
    Write-Host "✓ Java version: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java is not installed or not in PATH" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Clean build directory
Write-Host "🧹 Cleaning build directory..." -ForegroundColor Yellow
.\gradlew.bat clean --no-daemon
Write-Host ""

# Build Debug APK
Write-Host "🔨 Building Debug APK..." -ForegroundColor Yellow
.\gradlew.bat :app:assembleDebug --no-daemon --stacktrace
Write-Host ""

# Build Release APK (optional)
Write-Host "🔨 Building Release APK..." -ForegroundColor Yellow
.\gradlew.bat :app:assembleRelease --no-daemon --stacktrace
Write-Host ""

# Run Unit Tests
Write-Host "🧪 Running Unit Tests..." -ForegroundColor Yellow
.\gradlew.bat :app:testDebugUnitTest --no-daemon --stacktrace
Write-Host ""

# Check if APKs were created
$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release-unsigned.apk"

if (Test-Path $debugApk) {
    Write-Host "✅ Debug APK created: $debugApk" -ForegroundColor Green
    Get-Item $debugApk | Format-Table Name, Length, LastWriteTime -AutoSize
} else {
    Write-Host "❌ Debug APK not found" -ForegroundColor Red
}

Write-Host ""

if (Test-Path $releaseApk) {
    Write-Host "✅ Release APK created: $releaseApk" -ForegroundColor Green
    Get-Item $releaseApk | Format-Table Name, Length, LastWriteTime -AutoSize
} else {
    Write-Host "⚠️  Release APK not found (this is normal if unsigned)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🎉 Build completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "📦 Outputs:" -ForegroundColor Cyan
Write-Host "  - Debug APK: $debugApk"
Write-Host "  - Release APK: $releaseApk"
Write-Host "  - Test Results: app\build\test-results\"
Write-Host "  - Build Reports: app\build\reports\"

