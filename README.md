# SMS Automation App

[![Android Build CI](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/actions/workflows/android-build.yml/badge.svg)](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/actions/workflows/android-build.yml)

An Android application for bulk SMS sending and automation.

## Build Status

The project uses GitHub Actions for continuous integration. Every push to `main` or `master` triggers automated builds.

### Features
- Automated builds on push/PR
- Multiple JDK versions tested (17, 21)
- Debug and Release APK artifacts
- Unit test execution
- Build cache optimization

### Download APKs

After each successful build, APKs are available in the [Actions tab](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/actions):
- `app-debug-jdk17-apk` - Debug APK built with JDK 17
- `app-debug-jdk21-apk` - Debug APK built with JDK 21
- `app-release-jdk17-apk` - Release APK built with JDK 17
- `app-release-jdk21-apk` - Release APK built with JDK 21

Artifacts are retained for 30 days.

## Manual Build Trigger

You can manually trigger a build from the [Actions tab](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/actions/workflows/android-build.yml) with options:
- **All**: Build both debug and release variants
- **Debug**: Build only debug APK
- **Release**: Build only release APK

## Development

### Prerequisites
- JDK 17 or 21
- Android SDK
- Gradle (wrapper included)

### Build Locally

```bash
# Debug APK
./gradlew :app:assembleDebug

# Release APK
./gradlew :app:assembleRelease

# Run tests
./gradlew :app:testDebugUnitTest
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/          # Kotlin/Java source files
│   │   ├── res/           # Android resources
│   │   └── AndroidManifest.xml
│   └── test/              # Unit tests
├── build.gradle.kts
└── ...
```

## CI/CD Pipeline

The GitHub Actions workflow:
1. Checks out code
2. Sets up JDK (17 and 21 in parallel)
3. Caches Gradle dependencies
4. Cleans build directory
5. Builds Debug and/or Release APKs
6. Runs unit tests
7. Uploads artifacts (APKs, test results, build reports)

## License

[Add your license here]

---

**Note:** Replace `YOUR_USERNAME` and `YOUR_REPO_NAME` in the badge URLs with your actual GitHub username and repository name.

