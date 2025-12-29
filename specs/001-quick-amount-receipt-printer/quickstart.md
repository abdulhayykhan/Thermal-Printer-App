# Quickstart Guide: Quick Amount Receipt Printer

**Feature**: 001-quick-amount-receipt-printer  
**Version**: 1.0  
**Last Updated**: 2025-12-29

## Prerequisites

### Required Tools
- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: 17 or higher
- **Gradle**: 8.0+ (bundled with Android Studio)
- **Git**: For version control

### Hardware Requirements
- **Development Device**: Physical Android device (emulator does not support Bluetooth)
- **Target Device**: Android 8.0 (API 26) or higher
- **Thermal Printer**: Bluetooth-enabled ESC/POS thermal printer (58mm or 80mm)
  - Recommended: EPSON TM-T20, Star TSP143III, or generic XPrinter models
  - Must support Bluetooth Classic (not BLE)
  - Must be paired in Android Bluetooth settings before testing

### System Setup
1. Install Android Studio with Android SDK 34
2. Enable USB debugging on test device
3. Pair thermal printer with Android device via Bluetooth settings
4. Ensure device has Bluetooth permissions enabled

---

## Project Setup

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/thermal-printer-app.git
cd thermal-printer-app
git checkout 001-quick-amount-receipt-printer
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select **File → Open**
3. Navigate to project directory
4. Click **OK** and wait for Gradle sync

### 3. Verify Configuration

Check `build.gradle.kts` (Module level):
```kotlin
android {
    compileSdk = 34
    defaultConfig {
        minSdk = 26
        targetSdk = 34
    }
}
```

Check `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" />
```

### 4. Sync Dependencies

Click **Sync Now** in Android Studio or run:
```bash
./gradlew clean build
```

---

## Running the App

### Development Build

1. Connect Android device via USB
2. Verify device in **Run → Select Device**
3. Click **Run** (▶️) or press `Shift + F10`
4. Grant Bluetooth permissions when prompted
5. App launches and attempts to connect to last used printer

### Debug Build

```bash
./gradlew installDebug
adb shell am start -n com.naeem.thermalprinter/.presentation.MainActivity
```

### Logs

View logs in Android Studio Logcat or via ADB:
```bash
adb logcat | grep "ThermalPrinter"
```

---

## First-Time Usage

### Step 1: Grant Permissions

On first launch (Android 12+), the app will request:
- **Nearby devices** permission (BLUETOOTH_CONNECT)
- **Nearby devices** permission (BLUETOOTH_SCAN)

Tap **Allow** for both.

**Note**: On Android 11 and below, permissions are granted automatically at install.

### Step 2: Pair Printer

If printer not already paired:
1. Open Android **Settings → Bluetooth**
2. Turn on Bluetooth
3. Select thermal printer from available devices
4. Pair device (PIN may be required, commonly "0000" or "1234")
5. Return to app

### Step 3: Select Printer

1. If no printer connected, app shows "Disconnected" status
2. Tap status bar or device icon
3. Select paired printer from list
4. Wait for "Connected" status (~2-3 seconds)

### Step 4: Print First Receipt

1. Enter amount using number pad (e.g., `150.00`)
2. Verify amount displays correctly in SR currency
3. Tap **PRINT** button
4. Receipt prints immediately
5. Amount input clears on success

---

## Testing Checklist

### Manual Test Cases

#### TC1: Permission Flow (Android 12+)
- [ ] Launch app on fresh install
- [ ] Permissions dialog appears
- [ ] Grant both permissions
- [ ] App proceeds to main screen

#### TC2: Device Selection
- [ ] Status shows "Disconnected" with no saved printer
- [ ] Tap status bar
- [ ] Device selection dialog appears
- [ ] List shows paired Bluetooth devices
- [ ] Select printer
- [ ] Status changes to "Connected"

#### TC3: Auto-Reconnect
- [ ] Connect to printer successfully
- [ ] Close app (swipe away from recents)
- [ ] Relaunch app
- [ ] App auto-connects to last printer
- [ ] Status shows "Connected" within 3 seconds

#### TC4: Print Receipt
- [ ] Ensure printer connected
- [ ] Enter amount: `1234.56`
- [ ] Tap **PRINT** button
- [ ] Receipt prints with correct format
- [ ] Amount displays as "1234.56 SR"
- [ ] Date shows current timestamp
- [ ] Business name: "NAEEM DOCUMENTATION"

#### TC5: Error Handling
- [ ] Turn off printer
- [ ] Status changes to "Disconnected"
- [ ] Tap **PRINT** button
- [ ] Toast shows "Printer not connected"
- [ ] No crash or ANR

#### TC6: Input Validation
- [ ] Enter amount: `0.00`
- [ ] **PRINT** button disabled
- [ ] Enter amount: `10.50`
- [ ] **PRINT** button enabled

---

## Running Unit Tests

### Execute All Tests

```bash
./gradlew test
```

### Execute Specific Test Class

```bash
./gradlew test --tests "ReceiptBuilderTest"
./gradlew test --tests "MainViewModelTest"
```

### View Test Report

After running tests:
```bash
open app/build/reports/tests/testDebugUnitTest/index.html
```

### Key Test Files
- `ReceiptBuilderTest.kt`: ESC/POS command generation
- `BluetoothHelperTest.kt`: Bluetooth mock operations
- `MainViewModelTest.kt`: UI state management

---

## Running UI Tests

### Execute Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

**Note**: Requires physical device connected via USB.

### Execute Specific UI Test

```bash
./gradlew connectedAndroidTest --tests "MainScreenTest"
```

### View UI Test Report

```bash
open app/build/reports/androidTests/connected/index.html
```

---

## Troubleshooting

### Issue: Bluetooth permissions not granted

**Symptom**: App crashes on launch with SecurityException  
**Solution**:
1. Go to **Settings → Apps → Naeem Documentation → Permissions**
2. Enable **Nearby devices** (or Bluetooth on older Android)
3. Restart app

---

### Issue: Printer not found in device list

**Symptom**: Device selection dialog is empty  
**Solution**:
1. Ensure Bluetooth enabled on Android device
2. Pair printer in **Settings → Bluetooth** first
3. Restart app to refresh device list

---

### Issue: Connection timeout

**Symptom**: "Could not connect to printer" toast  
**Solution**:
1. Ensure printer is powered on and in range
2. Check printer is not connected to another device
3. Try turning printer off and on
4. Re-pair device in Bluetooth settings

---

### Issue: Print command sent but nothing prints

**Symptom**: "Print successful" but no output  
**Solution**:
1. Check printer has paper loaded correctly
2. Test printer with another app (e.g., printer manufacturer app)
3. Verify printer supports ESC/POS commands
4. Check printer error lights (paper jam, cover open, etc.)

---

### Issue: Receipt formatting incorrect

**Symptom**: Text misaligned or garbled  
**Solution**:
1. Ensure printer paper width matches (80mm preferred)
2. Check printer character set (UTF-8 support)
3. Review printer manual for ESC/POS compatibility
4. Some cheap printers have partial ESC/POS support

---

### Issue: App freezes on print

**Symptom**: ANR (Application Not Responding) dialog  
**Solution**: This indicates a threading issue
1. Check Logcat for stack trace
2. Verify all Bluetooth operations are on Dispatchers.IO
3. File bug report with stack trace

---

## Development Workflow

### Making Changes

1. Create feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make changes in appropriate layer:
   - Domain: `app/src/main/java/com/naeem/thermalprinter/domain/`
   - Data: `app/src/main/java/com/naeem/thermalprinter/data/`
   - Presentation: `app/src/main/java/com/naeem/thermalprinter/presentation/`

3. Add tests:
   - Unit: `app/src/test/java/com/naeem/thermalprinter/`
   - UI: `app/src/androidTest/java/com/naeem/thermalprinter/`

4. Run tests:
   ```bash
   ./gradlew test connectedAndroidTest
   ```

5. Manual test with real printer

6. Commit and push:
   ```bash
   git add .
   git commit -m "feat: your feature description"
   git push origin feature/your-feature-name
   ```

### Code Style

This project follows [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

Run linter:
```bash
./gradlew ktlintCheck
```

Auto-format:
```bash
./gradlew ktlintFormat
```

---

## Project Structure

```
app/src/main/java/com/naeem/thermalprinter/
├── domain/                    # Business logic
│   ├── model/                 # Data classes
│   └── printer/               # ESC/POS formatter
├── data/                      # External interfaces
│   ├── bluetooth/             # Bluetooth manager
│   └── repository/            # Persistence
└── presentation/              # UI layer
    ├── MainActivity.kt        # Entry point
    ├── MainViewModel.kt       # State management
    └── ui/                    # Compose screens
        ├── MainScreen.kt
        └── components/        # Reusable UI components
```

---

## Useful Commands

### Clean Build
```bash
./gradlew clean build
```

### Generate APK
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Install APK
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Uninstall App
```bash
adb uninstall com.naeem.thermalprinter
```

### View Current Activity
```bash
adb shell dumpsys activity activities | grep mResumedActivity
```

---

## Resources

### Documentation
- [Feature Specification](./spec.md)
- [Implementation Plan](./plan.md)
- [Data Model](./data-model.md)
- [Bluetooth Service Contract](./contracts/bluetooth-service.md)
- [Receipt Formatter Contract](./contracts/receipt-formatter.md)

### External References
- [Android Bluetooth Guide](https://developer.android.com/guide/topics/connectivity/bluetooth)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [ESC/POS Command Reference](https://reference.epson-biz.com/modules/ref_escpos/index.php)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)

---

## Support

### Reporting Issues

File issues on GitHub:
```
Title: [Component] Brief description
Body: 
- Expected behavior
- Actual behavior
- Steps to reproduce
- Android version
- Printer model
- Logs (adb logcat)
```

### Getting Help

1. Check this Quickstart Guide
2. Review feature specification
3. Check Logcat for error messages
4. Test with different printer (compatibility)
5. File GitHub issue with details

---

**Quickstart Status**: ✅ COMPLETE
