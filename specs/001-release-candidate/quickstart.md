# Release Build Quickstart Guide - Release Candidate v1.0

**Document Version**: 1.0  
**Date**: 2025-12-29  
**Feature**: Release Candidate v1.0 - Production-Ready Build  
**Status**: Ready for User Execution

## Quick Reference

```bash
# Step 1: Generate keystore (MANUAL - one time)
keytool -genkey -v -keystore naeem_release_key.jks \
  -alias naeem_documentation -keyalg RSA -keysize 2048 -validity 9125

# Step 2: Create keystore.properties (MANUAL)
cat > keystore.properties <<EOF
storeFile=/absolute/path/to/naeem_release_key.jks
storePassword=YOUR_PASSWORD
keyAlias=naeem_documentation
keyPassword=YOUR_PASSWORD
EOF

# Step 3: Add to .gitignore (MANUAL)
echo "keystore.properties" >> .gitignore

# Step 4: Build release APK (AUTOMATED)
./gradlew clean assembleRelease

# Step 5: Verify signature (AUTOMATED)
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk

# Step 6: Install on device (MANUAL)
adb install -r app/build/outputs/apk/release/app-release.apk
```

---

## Detailed Steps

### Prerequisites Checklist

Before starting, verify you have:

- ✅ Android Studio with SDK 34 installed
- ✅ JDK 11 configured in Android Studio
- ✅ At least one physical Android device (API 26+) connected via USB
- ✅ USB debugging enabled on test device
- ✅ Thermal printer paired via Bluetooth (optional for first test)

### Phase 1: Keystore Generation (MANUAL - One Time)

#### Step 1.1: Create Secure Directory (Recommended)

```bash
# Create a directory outside your project for secure storage
mkdir -p ~/secure_keys
cd ~/secure_keys
```

#### Step 1.2: Generate Release Keystore

```bash
keytool -genkey -v -keystore naeem_release_key.jks \
  -alias naeem_documentation \
  -keyalg RSA \
  -keysize 2048 \
  -validity 9125
```

**Interactive Prompts** (example answers):

```
Enter keystore password: MySecurePass123!@#
Re-enter new password: MySecurePass123!@#

What is your first and last name? [Unknown]: Your Name
What is the name of your organizational unit? [Unknown]: Development
What is the name of your organization? [Unknown]: Naeem Documentation
What is the name of your City or Locality? [Unknown]: Riyadh
What is the name of your State or Province? [Unknown]: SA
What is the two-letter country code for this unit? [Unknown]: SA
Is CN=Your Name, OU=Development, O=Naeem Documentation, L=Riyadh, ST=SA, C=SA correct? [no]: yes

Enter key password for <naeem_documentation> [Return if same as keystore password]: MySecurePass123!@#
```

**Output** (successful):

```
Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 9,125 days
for: CN=Your Name, OU=Development, O=Naeem Documentation, L=Riyadh, ST=SA, C=SA
[Storing ~/secure_keys/naeem_release_key.jks]
```

#### Step 1.3: Verify Keystore Creation

```bash
# Check keystore file exists
ls -lh ~/secure_keys/naeem_release_key.jks

# List contents of keystore
keytool -list -v -keystore ~/secure_keys/naeem_release_key.jks
# (Enter password when prompted)

# Expected output should show:
# Alias name: naeem_documentation
# Owner: CN=Your Name, OU=Development, O=Naeem Documentation, L=Riyadh, ST=SA, C=SA
# Signature algorithm name: SHA256withRSA
# Subject Public Key Info:
#   Algorithm: RSA
#   Key size: 2048 bits
# Valid from: [current date] to: [date + 25 years]
```

#### Step 1.4: Restrict File Permissions (Security)

```bash
# Make keystore readable only by owner
chmod 600 ~/secure_keys/naeem_release_key.jks

# Verify permissions (should show rw-------)
ls -la ~/secure_keys/naeem_release_key.jks
```

---

### Phase 2: Keystore Configuration (MANUAL - One Time)

#### Step 2.1: Create keystore.properties File

In your project root directory (same level as `build.gradle.kts`):

```bash
# Option 1: Using cat with heredoc
cat > keystore.properties <<EOF
storeFile=/home/YOUR_USERNAME/secure_keys/naeem_release_key.jks
storePassword=MySecurePass123!@#
keyAlias=naeem_documentation
keyPassword=MySecurePass123!@#
EOF

# Option 2: Using a text editor
nano keystore.properties
# Then paste:
# storeFile=/home/YOUR_USERNAME/secure_keys/naeem_release_key.jks
# storePassword=MySecurePass123!@#
# keyAlias=naeem_documentation
# keyPassword=MySecurePass123!@#
```

**⚠️ IMPORTANT**:
- Replace `/home/YOUR_USERNAME/` with your actual username
- Use **absolute paths**, not relative paths
- Passwords are **case-sensitive**
- Do **NOT commit** this file to git

#### Step 2.2: Verify keystore.properties in .gitignore

```bash
# Check if .gitignore exists
cat .gitignore | grep keystore.properties

# If not present, add it:
echo "keystore.properties" >> .gitignore

# Verify it was added
cat .gitignore | grep keystore.properties
```

#### Step 2.3: Test keystore.properties Loading

```bash
# Try building a release APK (will fail if passwords wrong, succeed if correct)
./gradlew assembleRelease

# If you get password errors, fix keystore.properties and try again
```

---

### Phase 3: Build Release APK (AUTOMATED)

#### Step 3.1: Clean Previous Builds

```bash
./gradlew clean
```

**Expected Output**:

```
> Task :app:clean UP-TO-DATE
> Task :clean
```

#### Step 3.2: Build Release APK with Gradle

```bash
./gradlew assembleRelease --info
```

**Expected Output** (last lines):

```
> Task :app:assembleRelease

BUILD SUCCESSFUL in 2m 45s
```

**Build takes**: 2-5 minutes depending on machine specs

#### Step 3.3: Verify APK Generation

```bash
# Check APK file exists
ls -lh app/build/outputs/apk/release/app-release.apk

# Example output:
# -rw-r--r-- 1 abdi abdi 8.2M Dec 29 15:45 app-release.apk
```

**Success Criteria**:
- ✅ File exists at correct path
- ✅ File size shown in megabytes (MB)
- ✅ **Size must be < 10 MB** (success: 8.2M in example above)

#### Step 3.4: Review ProGuard Warnings (Optional)

```bash
# Check build output for ProGuard warnings
./gradlew assembleRelease 2>&1 | grep -i "warning"

# Examples of acceptable warnings:
# - Unused classes
# - Unused methods
# - Unused fields

# Examples of errors (must fix):
# - Missing rules for Bluetooth classes
# - Missing rules for domain model classes
```

---

### Phase 4: Verify APK Signature (AUTOMATED)

#### Step 4.1: Verify with apksigner

```bash
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
```

**Expected Output**:

```
Verifies
Verified using v1 scheme (JAR signing): true
Verified using v2 scheme (full APK signing): true
Number of signers: 1
Signer #1 certificate DN: CN=Your Name, OU=Development, O=Naeem Documentation, L=Riyadh, ST=SA, C=SA
Signer #1 certificate SHA-256 digest: AA:BB:CC:DD:EE:FF:...
Signer #1 key algorithm: RSA
Signer #1 key size (bits): 2048
```

**Success Criteria**:
- ✅ Output starts with "Verifies"
- ✅ Both v1 and v2 schemes verified
- ✅ Certificate DN matches keystore certificate
- ✅ RSA 2048-bit key confirmed

#### Step 4.2: Troubleshoot Signature Issues

| Error | Cause | Solution |
|-------|-------|----------|
| `No signers found` | APK not signed | Check keystore.properties exists and has correct passwords |
| `FAILED` | Signature invalid | Keystore password incorrect or keystore corrupted |
| `SIGNERWITHLOWERTHANMIN` | Weak signature | Use RSA 2048-bit (default, should not occur) |

---

### Phase 5: Install on Physical Device (MANUAL)

#### Step 5.1: Connect Device via USB

```bash
# Verify device is connected
adb devices

# Expected output:
# List of attached devices
# DEVICE_SERIAL  device
```

#### Step 5.2: Install Release APK

```bash
# Simple install
adb install -r app/build/outputs/apk/release/app-release.apk

# Or install and clear app data (fresh install)
adb install -r --user 0 app/build/outputs/apk/release/app-release.apk

# Or uninstall old version first, then install
adb uninstall com.naeem.documentation && \
adb install app/build/outputs/apk/release/app-release.apk
```

**Expected Output**:

```
Success
```

**Troubleshooting**:

| Error | Cause | Solution |
|-------|-------|----------|
| `INSTALL_FAILED_INVALID_APK` | Corrupted APK | Rebuild: `./gradlew clean assembleRelease` |
| `INSTALL_FAILED_SECURITY` | Signature mismatch | Uninstall old version: `adb uninstall com.naeem.documentation` |
| `No devices found` | USB not connected | Check cable, enable USB debugging |

#### Step 5.3: Verify Installation on Device

```bash
# Check app is installed
adb shell pm list packages | grep naeem.documentation

# Expected output:
# package:com.naeem.documentation

# Check app size on device
adb shell du -sh /data/app/com.naeem.documentation*
```

---

### Phase 6: Test Release Build (MANUAL)

#### Test 1: App Launches Successfully

```bash
# Launch app from command line
adb shell am start -n com.naeem.documentation/.presentation.MainActivity

# Expected: App appears on device screen
```

#### Test 2: Verify App Identity

On the physical device:
1. Open app drawer
2. Look for "Naeem Documentation" app
3. Icon should show receipt/printer theme
4. Tap to launch app
5. Should see "Naeem Documentation" title in app

#### Test 3: Verify Permissions

On device:
1. App should prompt: "Allow Naeem Documentation to access Bluetooth?"
2. Grant or deny permission
3. If denied, app should show toast "Bluetooth permission is required..."
4. App should not crash

#### Test 4: Verify Bluetooth Connectivity (Optional)

1. Select printer from app
2. Should connect to paired printer
3. Should display "Connected: [Printer Name]"
4. Enter amount and print a test receipt

#### Test 5: View Logcat (Debug Info - Optional)

```bash
# Clear logcat
adb logcat -c

# Start logging
adb logcat | grep naeem

# Look for debug logs (should be absent in release build)
# Log.d() statements should NOT appear
```

---

### Phase 7: Archive Release APK (MANUAL)

#### Step 7.1: Document Release Details

```bash
# Create a release notes file
cat > RELEASE_NOTES_v1.0.txt <<EOF
Release: Naeem Documentation v1.0
Date: $(date)
APK Size: $(ls -lh app/build/outputs/apk/release/app-release.apk | awk '{print $5}')
Version Code: 1
Version Name: 1.0
Package: com.naeem.documentation

Features:
- Bluetooth thermal printer connectivity
- ESC/POS receipt printing
- Multiple printer support (58mm, 80mm)
- Material 3 UI with Jetpack Compose
- Permission handling for Bluetooth access

Tested On:
- Android 8.0 (API 26) - Minimum SDK
- Android 14 (API 34) - Target SDK

Known Limitations:
- Offline-only (no network connectivity)
- Single-screen UI (no history)
- Receipt printing only (no other document types)
EOF
```

#### Step 7.2: Copy APK to Archive Location

```bash
# Create release archive directory
mkdir -p ~/release_archive/v1.0

# Copy APK
cp app/build/outputs/apk/release/app-release.apk ~/release_archive/v1.0/

# Copy release notes
cp RELEASE_NOTES_v1.0.txt ~/release_archive/v1.0/

# List archive
ls -lh ~/release_archive/v1.0/
```

#### Step 7.3: Generate SHA-256 Checksum

```bash
# Generate checksum for integrity verification
sha256sum app/build/outputs/apk/release/app-release.apk > ~/release_archive/v1.0/app-release.apk.sha256

# Display checksum
cat ~/release_archive/v1.0/app-release.apk.sha256
```

---

## Command Reference

### Build Commands

```bash
# Clean build directory
./gradlew clean

# Build release APK (requires keystore.properties)
./gradlew assembleRelease

# Build debug APK (no signing required)
./gradlew assembleDebug

# Build with detailed logging
./gradlew assembleRelease --info

# Build without signing (if keystore.properties missing)
./gradlew assembleRelease -x verifyReleaseSigning
```

### Installation Commands

```bash
# Install signed APK
adb install -r app/build/outputs/apk/release/app-release.apk

# Uninstall app
adb uninstall com.naeem.documentation

# Launch app
adb shell am start -n com.naeem.documentation/.presentation.MainActivity

# Check installed packages
adb shell pm list packages | grep naeem

# View app size
adb shell du -sh /data/app/com.naeem.documentation*
```

### Verification Commands

```bash
# Verify APK signature
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk

# Check APK size
ls -lh app/build/outputs/apk/release/app-release.apk

# Generate SHA-256 checksum
sha256sum app/build/outputs/apk/release/app-release.apk

# View APK contents
aapt dump badging app/build/outputs/apk/release/app-release.apk
```

### Debugging Commands

```bash
# View logcat
adb logcat

# Filter logs for app
adb logcat | grep naeem

# Clear logcat
adb logcat -c

# View system errors
adb logcat *:E

# Capture logcat to file
adb logcat > logcat_$(date +%Y%m%d_%H%M%S).txt
```

---

## Common Issues & Fixes

| Issue | Error Message | Fix |
|-------|---------------|-----|
| Keystore not found | `keytool error: java.io.FileNotFoundException` | Verify keystore.properties has correct absolute path |
| Wrong password | `keytool error: Keystore was tampered with, or password was incorrect` | Re-check password in keystore.properties (case-sensitive) |
| APK not signed | `apksigner verify` returns "FAILED" | Ensure keystore.properties exists and is readable |
| APK too large | APK size > 10 MB | ProGuard/R8 may not be enabled; check `isMinifyEnabled = true` |
| Install fails | `INSTALL_FAILED_INVALID_APK` | APK may be corrupted; rebuild with `./gradlew clean assembleRelease` |
| Device not found | `error: no devices found` | Check USB cable, enable USB debugging, restart adb |

---

## Post-Release Checklist

After successful release APK generation, verify:

- [ ] APK builds without errors
- [ ] APK size < 10 MB
- [ ] APK signature verified with apksigner
- [ ] APK installs on test device without errors
- [ ] App launches and shows "Naeem Documentation"
- [ ] App icon displays receipt/printer theme
- [ ] Bluetooth permission prompt appears
- [ ] App grants/denies permissions correctly
- [ ] No crashes when permissions denied
- [ ] Printer connection works (if printer available)
- [ ] Receipt printing works (if printer available)
- [ ] Screen rotation handled correctly
- [ ] No debug logs in logcat (release build optimization)
- [ ] Release notes created and archived
- [ ] APK archived with SHA-256 checksum

---

## Next Steps

### For Distribution

1. **Upload to Google Play Store**: Use internal app sharing or closed testing track
2. **Share APK Directly**: Email or cloud storage (with SHA-256 checksum for verification)
3. **GitHub Releases**: Upload APK as release artifact with release notes

### For CI/CD Integration

1. **GitHub Actions**: Add workflow to build release APK on tag
2. **Environment Variables**: Store keystore password in GitHub Secrets
3. **Automated Testing**: Run unit tests and signed APK generation

### For Future Releases

1. **Increment versionCode**: 2, 3, 4, ... for each release
2. **Update versionName**: Follow semantic versioning (1.1, 1.2, 2.0, etc.)
3. **Update CHANGELOG**: Document features and fixes for each version
4. **Keep Keystore Safe**: Never lose or compromise the signing keystore

---

## References

- [Official Android App Signing Guide](https://developer.android.com/studio/publish/app-signing)
- [apksigner Documentation](https://developer.android.com/studio/command-line/apksigner)
- [Gradle Build Configuration](https://developer.android.com/build)
- [ProGuard/R8 Code Shrinking](https://developer.android.com/studio/build/shrink-code)

---

**Status**: ✅ Ready for Release Build Execution

This guide provides all steps needed to generate a signed, optimized release APK for production distribution. Follow Phase 1-7 sequentially for successful release build.
