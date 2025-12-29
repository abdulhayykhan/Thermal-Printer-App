# Release Candidate v1.0 - Build & Sign Guide

> **Environment Note**: This guide is for execution on your local machine with Android Studio, Java, and Gradle installed.

## Prerequisites Check

Verify you have:
```bash
# Check Java
java -version
# Expected: Java 11+ (OpenJDK 11 or higher)

# Check Gradle (via wrapper or globally)
./gradlew --version
# or
gradle --version
# Expected: Gradle 8.0+

# Check Android SDK
echo $ANDROID_HOME
# Should show Android SDK path
```

---

## Step 1: Create Gradle Wrapper (if missing)

If `./gradlew` doesn't exist, generate it:

```bash
cd /path/to/thermal-printer-app

# Using Gradle command
gradle wrapper --gradle-version 8.0

# Or directly download wrapper files
mkdir -p gradle/wrapper
curl -o gradle/wrapper/gradle-wrapper.jar \
  https://services.gradle.org/distributions/gradle-8.0-bin.zip
```

Verify wrapper was created:
```bash
ls -la gradlew
ls -la gradle/wrapper/
```

---

## Step 2: Generate Release Keystore

Create a signing key for your release APK:

```bash
keytool -genkey -v \
  -keystore naeem_release_key.jks \
  -alias naeem_key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

**Prompts you'll see:**
```
Enter keystore password: [STRONG PASSWORD - save this!]
Re-enter new password: [SAME PASSWORD]
What is your first and last name? Naeem Documentation
What is the name of your organizational unit? Engineering
What is the name of your organization? Naeem
What is the name of your city or locality? [Your City]
What is the name of your state or province? [Your State]
What are the two-letter country code? [e.g., US]

Is CN=Naeem Documentation, OU=Engineering, O=Naeem, L=[City], ST=[State], C=[Code] correct? yes

Enter key password for <naeem_key>: [Can press Enter to use keystore password]
```

**Verify keystore was created:**
```bash
ls -lh naeem_release_key.jks
keytool -list -v -keystore naeem_release_key.jks
```

---

## Step 3: Create keystore.properties

Create a properties file with your keystore credentials:

```bash
cat > keystore.properties << EOF
storeFile=naeem_release_key.jks
storePassword=YOUR_KEYSTORE_PASSWORD_HERE
keyAlias=naeem_key
keyPassword=YOUR_KEY_PASSWORD_HERE
EOF
```

**Important**: This file is git-ignored for security. Don't commit credentials to Git.

---

## Step 4: Clean Build

Clean the project first:

```bash
./gradlew clean
```

Expected output:
```
> Task :clean
BUILD SUCCESSFUL in XXs
```

---

## Step 5: Build Release APK

Generate the signed release APK:

```bash
./gradlew assembleRelease --info
```

**Expected output:**
```
> Task :app:compileReleaseKotlin
> Task :app:mergeReleaseResources
> Task :app:compileReleaseResources
> Task :app:createReleaseApkListingFileRedirect
> Task :app:processReleaseMainManifest
> Task :app:processReleaseResources
> Task :app:signReleaseBundle
> Task :app:packageRelease
> Task :app:assembleRelease

BUILD SUCCESSFUL in XXs
```

---

## Step 6: Verify Release APK

Check that the APK was created and is signed:

```bash
# Find APK
ls -lh app/build/outputs/apk/release/

# Verify signature (requires apksigner from Android SDK)
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
```

Expected output:
```
app-release.apk:
  Verified using v1 scheme (JAR signing): true
  Verified using v2 scheme (full APK signing): true
  Verified using v4 scheme (signing block): false
  Number of signers: 1
```

---

## Step 7: Transfer to Device

Transfer the APK to your Android device:

```bash
# Option A: Using ADB
adb install app/build/outputs/apk/release/app-release.apk

# Option B: Manual transfer
# Copy app-release.apk to phone via USB/email/cloud storage
# Then open file manager and tap to install
```

---

## Step 8: Test on Device

Once installed, verify:

### ✅ Permission Handling
- [ ] Launch app
- [ ] Grant "Nearby devices" permission (BLUETOOTH_CONNECT/BLUETOOTH_SCAN)
- [ ] App displays "Naeem Documentation" title
- [ ] No crashes on permission screens

### ✅ Bluetooth Connectivity
- [ ] Turn on Bluetooth on device
- [ ] Tap device icon in app
- [ ] Select paired thermal printer
- [ ] Status shows "Connected" within 3 seconds
- [ ] No connection errors

### ✅ Receipt Printing
- [ ] Enter amount: `150.50`
- [ ] Tap PRINT button
- [ ] Receipt prints successfully
- [ ] Verify receipt shows:
  - "Naeem Documentation" (business name)
  - Amount: "150.50 SR"
  - Current timestamp
  - Proper formatting

### ✅ Error Handling
- [ ] Turn off printer
- [ ] Tap PRINT button
- [ ] App shows "Printer not connected" toast
- [ ] App does not crash

---

## Step 9: View APK Info

Get detailed APK information:

```bash
# APK size
ls -lh app/build/outputs/apk/release/app-release.apk

# APK contents
unzip -l app/build/outputs/apk/release/app-release.apk | head -20

# AndroidManifest info
aapt dump badging app/build/outputs/apk/release/app-release.apk | grep -E "package|versionCode|versionName"
```

Expected:
```
package: name='com.naeem.documentation' versionCode='1' versionName='1.0'
```

---

## Troubleshooting

### Build Fails: "Keystore not found"
**Solution**: Ensure `keystore.properties` exists and paths are correct:
```bash
ls -la naeem_release_key.jks
cat keystore.properties
```

### Build Fails: "Keystore password incorrect"
**Solution**: Verify credentials in `keystore.properties` match keystore creation.

### APK size too large (>15MB)
**Solution**: Verify ProGuard/R8 is enabled:
```bash
# Check build.gradle.kts release block
grep -A 5 "release {" app/build.gradle.kts
# Should show: isMinifyEnabled = true
```

### APK signature verification fails
**Solution**: Ensure both V1 and V2 signatures are enabled:
```bash
# In Android Studio: Build > Generate Signed Bundle/APK
# Check: "V1 (Jar Signature)" ✓
# Check: "V2 (Full APK Signature)" ✓
```

### App crashes on install
**Solution**: 
1. Uninstall previous debug version: `adb uninstall com.naeem.documentation`
2. Reinstall: `adb install app/build/outputs/apk/release/app-release.apk`

### Bluetooth permissions denied on Android 12+
**Solution**: 
1. Go to Settings → Apps → Naeem Documentation → Permissions
2. Enable "Nearby devices" or "Bluetooth"
3. Restart app

---

## Build Configuration Summary

This release is configured with:

```gradle
android {
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.naeem.documentation"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true          // Enable ProGuard
            isShrinkResources = true        // Shrink unused resources
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.release
        }
    }
}
```

---

## Release Checklist

- [ ] Java 11+ installed
- [ ] Gradle 8.0+ available (via wrapper or globally)
- [ ] Gradle wrapper created (if needed)
- [ ] Keystore generated (`naeem_release_key.jks`)
- [ ] keystore.properties created with credentials
- [ ] `./gradlew clean` runs successfully
- [ ] `./gradlew assembleRelease` builds without errors
- [ ] `apksigner verify` confirms both V1 and V2 signatures
- [ ] APK size < 10MB
- [ ] APK installed on physical device
- [ ] Permissions handled gracefully
- [ ] Bluetooth connects successfully
- [ ] Receipt prints with correct branding
- [ ] All error cases handled without crashes

---

## Next Steps

After successful build and testing:

1. **Tag the release**:
   ```bash
   git tag -a v1.0 -m "Release Candidate v1.0 - Production Build"
   git push origin v1.0
   ```

2. **Create GitHub Release**:
   - Go to GitHub repo → Releases → Create new release
   - Tag: `v1.0`
   - Title: "Naeem Documentation v1.0"
   - Upload `app-release.apk`
   - Describe key features

3. **Distribute APK**:
   - Email to users
   - Upload to internal testing platform
   - Prepare for Google Play submission (if planned)

---

## Support

For issues, check:
- `specs/001-release-candidate/quickstart.md` - Detailed development guide
- `specs/001-release-candidate/contracts/signing-config.md` - Signing details
- `EXECUTION_REPORT_v1.md` - Implementation status

Last updated: 2025-12-29
