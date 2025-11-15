# Quick Test Guide

## The Fix
I've fixed the **blank white screen issue**. The problem was missing `layout_width` and `layout_height` attributes on MaterialButtons and TextViews in your layout file.

## What to Do Now

### 1. Rebuild the App
In Android Studio:
- Click: **Build** → **Clean Project**
- Then: **Build** → **Rebuild Project**
- Wait for build to complete

### 2. Run on Emulator
- Click the **Run** button (green play icon)
- Or press **Shift+F10**

### 3. What You Should See
✅ App launches (no ANR)
✅ Full UI is visible with:
   - Top toolbar with "SMS Automation - USSD"
   - Three buttons: Manage, Import, Contacts
   - Two cards showing SMS and Token status
   - Statistics buttons: Completed and Failed
   - "Recent Transactions" text
   - Floating action button (orange play button)

✅ **Every 5 seconds**, the numbers should auto-update:
   - Token count increases
   - SMS count decreases
   - Completed/Failed stats increase

### 4. If You Still See Issues

**Check Logcat:**
```
View → Tool Windows → Logcat
```

Filter by "MainActivity" and look for:
- ✅ "onCreate started"
- ✅ "Inflating binding..."
- ✅ "Content view set successfully"
- ❌ Any "FATAL ERROR" messages

**If blank screen persists:**
1. Uninstall the app from emulator completely
2. Clean project again
3. Cold boot the emulator (Device Manager → Cold Boot)
4. Reinstall

## Key Changes Made

### Fixed Files:
1. **activity_main.xml** - Added missing layout attributes to 11 views
2. **MainActivity.kt** - Better logging and lifecycle management
3. **DarajaApi.kt** - Network timeouts to prevent hangs
4. **App.kt** (new) - StrictMode for debugging

### What Was Wrong:
Every MaterialButton and most TextViews were missing:
```xml
android:layout_width="wrap_content"
android:layout_height="wrap_content"
```

Without these, Android couldn't inflate the layout, resulting in a blank screen.

## Success Indicators
- ✅ No "App isn't responding" dialog
- ✅ UI appears immediately (< 1 second)
- ✅ All buttons and text are visible
- ✅ Numbers update every 5 seconds
- ✅ No crashes in Logcat

## Need Help?
If the issue persists, share:
1. Screenshot of the emulator
2. Logcat output filtered by "MainActivity"
3. Any error dialogs

