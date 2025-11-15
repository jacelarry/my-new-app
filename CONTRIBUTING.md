# Contributing to SMS Automation App

Thank you for your interest in contributing! This document provides guidelines and information about the development workflow.

## Development Setup

### Prerequisites
- JDK 17 or 21 (both are tested in CI)
- Android Studio (latest stable version recommended)
- Git

### Clone and Build

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
cd YOUR_REPO_NAME
./gradlew build
```

## CI/CD Pipeline

### Automated Builds

Every push or pull request to `main` or `master` branches triggers automated builds:

- **Matrix Strategy**: Builds are tested on both JDK 17 and JDK 21
- **Variants**: Both debug and release APKs are built
- **Tests**: Unit tests are executed automatically
- **Artifacts**: APKs and test results are uploaded and retained for 30 days

### Manual Builds

You can trigger manual builds from the GitHub Actions tab with custom options:
1. Go to Actions → Android Build CI → Run workflow
2. Select the branch
3. Choose build variant (all/debug/release)
4. Click "Run workflow"

### Build Status

Check the build status badge on the README. Click it to see detailed build logs and download APK artifacts.

## Pull Request Guidelines

1. **Fork the repository** and create your branch from `main`
2. **Write clear commit messages** following conventional commits format:
   - `feat:` for new features
   - `fix:` for bug fixes
   - `docs:` for documentation changes
   - `refactor:` for code refactoring
   - `test:` for adding tests
   - `ci:` for CI/CD changes

3. **Ensure builds pass**: Your PR must pass all CI checks before merging
4. **Add tests** for new features
5. **Update documentation** if needed

## Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused

## Testing

### Run Tests Locally

```bash
# All tests
./gradlew test

# Debug unit tests only
./gradlew :app:testDebugUnitTest

# Generate coverage report
./gradlew jacocoTestReport
```

### CI Test Execution

Tests run automatically on every push/PR. Test results are uploaded as artifacts:
- View test reports in the Actions tab
- Download `test-results-jdk17` or `test-results-jdk21` artifacts

## Build Artifacts

After each successful build, the following artifacts are available:

| Artifact Name | Description | Retention |
|--------------|-------------|-----------|
| `app-debug-jdk17-apk` | Debug APK (JDK 17) | 30 days |
| `app-debug-jdk21-apk` | Debug APK (JDK 21) | 30 days |
| `app-release-jdk17-apk` | Release APK (JDK 17) | 30 days |
| `app-release-jdk21-apk` | Release APK (JDK 21) | 30 days |
| `test-results-jdk*` | Unit test results | 14 days |
| `build-reports-jdk*` | Build reports (on failure) | 7 days |

## Troubleshooting Build Issues

### Common Issues

1. **Gradle sync failures**
   - Clear cache: `./gradlew clean --refresh-dependencies`
   - Invalidate caches in Android Studio

2. **Build failures in CI but works locally**
   - Check JDK version (CI uses JDK 17 and 21)
   - Ensure no local.properties or secrets in commits

3. **Resource compilation errors**
   - Validate XML files are well-formed
   - Check for missing resources

### Getting Help

- Open an issue for bugs or questions
- Check existing issues before creating new ones
- Provide detailed error logs and reproduction steps

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

**Note:** Replace `YOUR_USERNAME` and `YOUR_REPO_NAME` with actual values.

