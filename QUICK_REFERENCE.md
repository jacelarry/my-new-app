# CI/CD Quick Reference Card

## 🚀 Quick Commands

### Local Build (Windows)
```powershell
.\build-local.ps1
```

### Local Build (Linux/Mac)
```bash
./build-local.sh
```

### Manual Gradle Commands
```bash
# Clean
./gradlew clean

# Build Debug APK
./gradlew :app:assembleDebug

# Build Release APK
./gradlew :app:assembleRelease

# Run Unit Tests
./gradlew :app:testDebugUnitTest

# Build Everything
./gradlew build
```

## 📦 APK Locations

```
Debug:   app/build/outputs/apk/debug/app-debug.apk
Release: app/build/outputs/apk/release/app-release-unsigned.apk
```

## 🔄 CI/CD Status

**Workflow File:** `.github/workflows/android-build.yml`

**Triggers:**
- Push to `main` or `master`
- Pull request to `main` or `master`
- Manual dispatch via GitHub Actions tab

**Build Matrix:**
- JDK 17
- JDK 21

**Artifacts (Retention):**
- APKs: 30 days
- Test Results: 14 days
- Build Reports: 7 days

## 🏷️ Badges

```markdown
[![Android Build CI](https://github.com/jacelarry/my-new-app/actions/workflows/android-build.yml/badge.svg)](https://github.com/jacelarry/my-new-app/actions/workflows/android-build.yml)
```

## 🔧 Troubleshooting

### Clear Gradle Cache
```bash
./gradlew clean --refresh-dependencies
```

### Check Build Cache
```bash
./gradlew --build-cache --info
```

### Validate XML Resources
```bash
./gradlew :app:mergeDebugResources
```

## 📱 Install APK to Device

### Via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### List Connected Devices
```bash
adb devices
```

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test
```bash
./gradlew :app:testDebugUnitTest --tests "com.example.YourTestClass"
```

### Generate Coverage Report
```bash
./gradlew jacocoTestReport
```

## 📊 Reports Location

```
Test Results:  app/build/test-results/
HTML Reports:  app/build/reports/tests/
Lint Reports:  app/build/reports/lint/
Coverage:      app/build/reports/jacoco/
```

## 🔐 Keystore (Release Signing)

Store keystore credentials in GitHub Secrets:
- `KEYSTORE_FILE` (base64 encoded)
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

## 🤖 Dependabot

**Schedule:** Every Monday at 9:00 AM
**PRs:** Up to 10 for Gradle, 5 for Actions
**Auto-merge:** Not enabled (review required)

## 📝 Commit Message Format

```
feat: add new feature
fix: resolve bug
docs: update documentation
refactor: code improvement
test: add tests
ci: CI/CD changes
chore: maintenance tasks
```

## 🔗 Quick Links

- [Actions Tab](https://github.com/jacelarry/my-new-app/actions)
- [Releases](https://github.com/jacelarry/my-new-app/releases)
- [Issues](https://github.com/jacelarry/my-new-app/issues)
- [Pull Requests](https://github.com/jacelarry/my-new-app/pulls)

---

**Tip:** Bookmark this file for quick reference!

