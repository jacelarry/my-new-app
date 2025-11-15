# Codemagic CI/CD Setup Guide

## 🚀 Quick Start

Your `codemagic.yaml` file has been created at the project root with three workflows:

1. **android-workflow** - Full build (Debug + Release + Tests)
2. **android-release-workflow** - Production release (triggered by tags)
3. **android-debug-workflow** - Fast debug build for PRs

---

## 📋 Setup Steps

### Step 1: Create Codemagic Account
1. Go to [https://codemagic.io/](https://codemagic.io/)
2. Sign up with GitHub, GitLab, or Bitbucket
3. Grant repository access

### Step 2: Add Your Repository
1. Click **Add application**
2. Select your Git provider
3. Choose `sms-automation-project/smsautomationapp` repository
4. Select **Android** as project type

### Step 3: Push the Configuration File

The `codemagic.yaml` file is ready! Now push it to your repository:

```bash
# From your project root directory
git add codemagic.yaml
git commit -m 'Add Codemagic CI/CD configuration'
git push origin main
```

Or if using `master` branch:
```bash
git push origin master
```

### Step 4: Detect Configuration in Codemagic
1. Go to your Codemagic dashboard
2. Select your application
3. Click **"Check for configuration file"** button (top right)
4. Codemagic will detect the `codemagic.yaml` file
5. Your workflows will appear in the UI

---

## 🔧 Configuration Overview

### Workflows Included

#### 1. **android-workflow** (Main Build)
- **Triggers:** Push to `main`, `master`, or `develop` branches
- **Builds:** Debug APK, Release APK
- **Tests:** Unit tests
- **Artifacts:** APKs, AABs, test results, ProGuard mappings
- **Duration:** Up to 120 minutes
- **Instance:** Mac Mini M1

#### 2. **android-release-workflow** (Production Release)
- **Triggers:** Git tags matching `v*` (e.g., `v1.0.0`)
- **Builds:** Release AAB + Release APK
- **Publishing:** Google Play (production track with 10% rollout)
- **Artifacts:** Signed AAB, APK, ProGuard mappings
- **Duration:** Up to 120 minutes

#### 3. **android-debug-workflow** (Fast PR Builds)
- **Triggers:** Pull requests
- **Builds:** Debug APK only
- **Tests:** Unit tests
- **Duration:** Up to 60 minutes (faster feedback)
- **No signing required**

---

## 🔐 Required Environment Variables

### For Signing (android-workflow & android-release-workflow)

1. **Upload Keystore to Codemagic:**
   - Go to **Teams** → **Integrations** → **Code signing identities**
   - Upload your keystore file
   - Set reference name: `keystore_reference`

2. **Required Environment Variables:**
   ```
   CM_KEYSTORE_PASSWORD=your_keystore_password
   CM_KEY_ALIAS=your_key_alias
   CM_KEY_PASSWORD=your_key_password
   ```

### For Google Play Publishing (Optional)

1. **Create Google Play Service Account:**
   - Go to Google Play Console
   - Setup → API access → Create service account
   - Grant permissions: Release manager
   - Download JSON key

2. **Add to Codemagic:**
   - Go to **Teams** → **Integrations** → **Google Play**
   - Upload the JSON key file
   - Create group: `google_play`
   - Add variable: `GCLOUD_SERVICE_ACCOUNT_CREDENTIALS`

### Email Notifications

Update the email address in `codemagic.yaml`:
```yaml
email:
  recipients:
    - your-email@example.com  # Change this!
```

### Slack Notifications (Optional)

1. Create Slack webhook
2. Add to Codemagic environment variables:
   ```
   SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/WEBHOOK/URL
   ```
3. Update channel name in `codemagic.yaml`:
   ```yaml
   slack:
     channel: '#your-channel'
   ```

---

## 🎯 Usage Examples

### Trigger Main Workflow
```bash
git add .
git commit -m "feat: add new feature"
git push origin main
```

### Trigger Release Workflow
```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

### Trigger Debug Workflow
```bash
# Create a pull request on GitHub/GitLab
# The workflow will trigger automatically
```

---

## 📦 Artifacts Generated

After each build, download artifacts from the Codemagic dashboard:

| Workflow | Artifacts |
|----------|-----------|
| **android-workflow** | `app-debug.apk`, `app-release.apk`, `test-results.xml`, `reports/` |
| **android-release-workflow** | `app-release.aab`, `app-release.apk`, `mapping.txt` |
| **android-debug-workflow** | `app-debug.apk`, `test-results.xml` |

---

## 🔄 Build Status Badge

Add this badge to your README.md:

```markdown
[![Codemagic build status](https://api.codemagic.io/apps/YOUR_APP_ID/status_badge.svg)](https://codemagic.io/apps/YOUR_APP_ID/latest_build)
```

**To get your APP_ID:**
1. Go to Codemagic dashboard
2. Select your app
3. Click **Settings** → **General**
4. Copy the **Application ID**

---

## 🛠️ Customization Options

### Change Java Version
```yaml
environment:
  java: 17  # or 11, 21
```

### Add Build Variants
```yaml
scripts:
  - name: Build Staging
    script: ./gradlew assembleStagingDebug
```

### Enable Instrumented Tests
```yaml
scripts:
  - name: Run instrumented tests
    script: ./gradlew connectedAndroidTest
```

### Add Lint Checks
```yaml
scripts:
  - name: Run Android Lint
    script: ./gradlew lint
```

### Cache Gradle Dependencies
```yaml
cache:
  cache_paths:
    - ~/.gradle/caches
    - ~/.gradle/wrapper
```

---

## 🐛 Troubleshooting

### Issue: "No signing configuration found"
**Solution:** Upload your keystore to Codemagic and set environment variables

### Issue: "SDK not found"
**Solution:** The `local.properties` script handles this automatically

### Issue: "Build timeout"
**Solution:** Increase `max_build_duration` or optimize build scripts

### Issue: "Tests failed but build continues"
**Solution:** Remove `ignore_failure: true` from test scripts

### Issue: "Google Play publishing fails"
**Solution:** Verify service account has correct permissions in Play Console

---

## 📊 Comparison: Codemagic vs GitHub Actions

| Feature | Codemagic | GitHub Actions |
|---------|-----------|----------------|
| Setup Complexity | Lower (GUI + YAML) | Medium (YAML only) |
| Build Minutes (Free) | 500 min/month | 2000 min/month |
| macOS Builds | ✅ Included | ✅ Included |
| Android Signing | ✅ Built-in | ⚙️ Manual setup |
| Google Play Publishing | ✅ One-click | ⚙️ Manual setup |
| Build Speed | Fast (M1 Macs) | Fast |
| Artifact Storage | 30 days | 90 days (configurable) |

---

## 🔗 Useful Links

- [Codemagic Documentation](https://docs.codemagic.io/)
- [Android Configuration Guide](https://docs.codemagic.io/yaml-quick-start/building-a-native-android-app/)
- [Code Signing Guide](https://docs.codemagic.io/yaml-code-signing/signing-android/)
- [Google Play Publishing](https://docs.codemagic.io/yaml-publishing/google-play/)
- [Codemagic Pricing](https://codemagic.io/pricing/)

---

## 🎉 You're Ready!

Your Codemagic CI/CD pipeline is configured and ready to use!

### Next Actions:
1. ✅ Push `codemagic.yaml` to repository
2. ✅ Click "Check for configuration file" in Codemagic
3. ✅ Configure environment variables (keystore, email)
4. ✅ Trigger your first build
5. ✅ Download and test APKs

**Happy building! 🚀**

---

*Configuration created: November 15, 2025*
*Workflows: 3 | Supported branches: main, master, develop*

