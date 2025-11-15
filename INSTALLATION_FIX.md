# Installation Error Fix Guide

## Error: "Unknown failure" during app installation

The "Unknown failure" error typically indicates a communication issue between Android Studio and your device/emulator.

### Solutions (Try in order):

#### **1. Clear Build Cache & Rebuild**
- Click: `Build` → `Clean Project`
- Click: `Build` → `Rebuild Project`
- Then: `Run` → `Run 'app'`

#### **2. Uninstall Previous App Versions**
**From Android Studio Terminal:**
```bash
adb uninstall com.example.smsautomationapp
```

**Then rebuild and reinstall.**

#### **3. Check Device/Emulator Connection**
```bash
adb devices
```
Your device should appear as "device" (not "unauthorized" or "offline").

If offline/unauthorized:
- Disconnect USB and reconnect
- Grant USB debugging permission on device
- Restart ADB: `adb kill-server` then `adb devices`

#### **4. For Emulator Issues**
If using an emulator:
- Close the emulator
- Go to: `Tools` → `Device Manager`
- Click the ⏯️ button to restart the emulator
- Wait for it to fully boot
- Retry running the app

#### **5. Check Available Storage**
Ensure your device/emulator has at least 500MB free space.

#### **6. Force Stop & Clear App Data**
```bash
adb shell pm disable com.example.smsautomationapp
adb shell pm clear com.example.smsautomationapp
adb shell pm enable com.example.smsautomationapp
```

#### **7. Check APK Configuration**
The following debug signing config has been added to your `app/build.gradle.kts`:
```kotlin
debug {
    isDebuggable = true
    isMinifyEnabled = false
}
```

#### **8. Verify Dependencies**
Run: `File` → `Sync Now` in Android Studio to ensure all Gradle dependencies are synced.

#### **9. Check Manifest Integrity**
The AndroidManifest.xml references these activities:
- ✅ MainActivity
- ✅ TillSettingsActivity  
- ✅ SubscriptionActivity
- ✅ BundlesActivity
- ✅ FCMService

All required files exist in the project.

#### **10. Last Resort: Restart IDE & Clean System**
- Close Android Studio completely
- Delete the `build/` folder from the project
- Restart Android Studio
- Sync Gradle
- Rebuild and install

### Debug Tips:
- Check `Logcat` (View → Tool Windows → Logcat) for detailed error messages
- Look for error messages starting with "Failure" or "Error"
- Screenshot the full error message for more specific troubleshooting

### Known Issues Resolved:
✅ MainActivity crash issues (fixed with lifecycleScope)
✅ Debug signing configuration (added)
✅ Proper error handling in coroutines (implemented)

