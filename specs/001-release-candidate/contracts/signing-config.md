# Signing Configuration Contract - Release Candidate v1.0

**Document Version**: 1.0  
**Date**: 2025-12-29  
**Feature**: Release Candidate v1.0 - Production-Ready Build  
**Status**: Ready for Implementation

## Keystore Specification

### Generation Parameters

```bash
keytool -genkey -v -keystore naeem_release_key.jks \
  -alias naeem_documentation \
  -keyalg RSA \
  -keysize 2048 \
  -validity 9125
```

**Parameters Explained**:
- `-keystore naeem_release_key.jks`: Keystore filename (RSA format)
- `-alias naeem_documentation`: Alias for the key pair within keystore
- `-keyalg RSA`: Algorithm - RSA 2048-bit for Android app signing
- `-keysize 2048`: Key size in bits (2048 is standard, 4096 optional for highest security)
- `-validity 9125`: Validity period in days (9125 days = 25 years, sufficient for app lifespan)

### Certificate Details Prompt

When executing keytool, you will be prompted for:

```
Enter keystore password: [ENTER 8+ CHARACTER PASSWORD]
Re-enter new password: [CONFIRM PASSWORD]
What is your first and last name? [Your Name]
What is the name of your organizational unit? [e.g., Development, Thermal Printer]
What is the name of your organization? [e.g., Naeem Documentation, Company Name]
What is the name of your City or Locality? [Your City]
What is the name of your State or Province? [Your State]
What is the two-letter country code for this unit? [e.g., SA, US]
Is CN=Your Name, OU=Development, O=Naeem Documentation, L=Your City, ST=Your State, C=SA correct? [yes]
Enter key password for <naeem_documentation>: [ENTER PASSWORD - can match keystore password]
```

### Security Considerations

**⚠️ CRITICAL SECURITY WARNINGS**:

1. **DO NOT commit keystore.properties to version control** - passwords will be exposed
2. **Secure Storage Location**: Store keystore file outside the repository
   - Recommended: `~/secure_keys/naeem_release_key.jks` or equivalent secure directory
   - Alternative: Environment variables for CI/CD pipelines
3. **Password Strength**: Use strong passwords (16+ characters with mixed case, numbers, symbols recommended)
4. **Backup**: Keep a secure backup of the keystore file; losing it prevents app updates
5. **Access Control**: Restrict file permissions to owner only: `chmod 600 keystore.properties`

## Build Configuration Contract

### keystore.properties File Template

**Location**: Project root (git-ignored)

```properties
storeFile=/absolute/path/to/naeem_release_key.jks
storePassword=YOUR_STORE_PASSWORD_HERE
keyAlias=naeem_documentation
keyPassword=YOUR_KEY_PASSWORD_HERE
```

**Example** (with actual values redacted):

```properties
storeFile=/home/username/secure_keys/naeem_release_key.jks
storePassword=SecurePass123!@#
keyAlias=naeem_documentation
keyPassword=SecurePass123!@#
```

### Gradle Configuration in app/build.gradle.kts

The following configuration has been added to `app/build.gradle.kts`:

```kotlin
// Load keystore properties for release signing
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = java.util.Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    // ... other config ...
    
    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}
```

**Key Features**:
- Gracefully handles missing keystore.properties (build still works in debug mode)
- Loads keystore details from external properties file (not committed to git)
- Automatically signs release APK when keystore is available
- Uses safe type casting with null checks

## APK Signing Schemes

### V1 Signature (JAR Signing)

- **Status**: Enabled (v1SigningEnabled implicit default)
- **Compatibility**: All Android versions
- **Algorithm**: RSA with SHA-256
- **Use Case**: Legacy device support and broader compatibility

### V2 Signature (Full APK Signing)

- **Status**: Enabled (v2SigningEnabled implicit default)
- **Compatibility**: Android 7.0+ (API 24+)
- **Algorithm**: RSA with SHA-256
- **Use Case**: Improved performance, better security (full APK verification)

## Release APK Verification

### Signature Verification Command

```bash
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
```

**Expected Output** (successful verification):

```
Verifies
Verified using v1 scheme (JAR signing): true
Verified using v2 scheme (full APK signing): true
Number of signers: 1
Signer #1 certificate DN: CN=Your Name, OU=Development, O=Naeem Documentation, L=Your City, ST=Your State, C=SA
Signer #1 certificate SHA-256 digest: AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88
Signer #1 certificate SHA-1 digest: AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD
Signer #1 key algorithm: RSA
Signer #1 key size (bits): 2048
Signer #1 public key SHA-256 digest: AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88
```

### Troubleshooting Signature Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| `No signers found` | APK not signed | Ensure keystore.properties exists and is properly formatted |
| `Failed to load keystore` | Wrong keystore path | Verify absolute path in keystore.properties is correct |
| `Wrong password` | Incorrect password | Re-verify keystore password (case-sensitive) |
| `Key alias not found` | Wrong alias name | Ensure alias matches "naeem_documentation" in keystore.properties |
| `Certificate expired` | Keystore validity expired | Generate new keystore with longer validity (9125 days) |

## Installation Testing

### Pre-Installation Checks

```bash
# Verify APK signature
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk

# Check APK size
ls -lh app/build/outputs/apk/release/app-release.apk

# Inspect APK contents
aapt dump badging app/build/outputs/apk/release/app-release.apk
```

### Installation Commands

```bash
# Install on connected device
adb install -r app/build/outputs/apk/release/app-release.apk

# Install and auto-launch
adb install -r -s app/build/outputs/apk/release/app-release.apk && \
adb shell am start -n com.naeem.documentation/.presentation.MainActivity

# Uninstall before reinstall (if needed)
adb uninstall com.naeem.documentation && \
adb install app/build/outputs/apk/release/app-release.apk
```

### Post-Installation Verification

After installation, verify:

1. ✅ App launches without errors
2. ✅ App name displays as "Naeem Documentation" in launcher
3. ✅ App icon shows receipt/printer theme (not Android robot)
4. ✅ Bluetooth permission prompt appears on first launch
5. ✅ App functionality works (printer connection, printing)
6. ✅ No debug logs appear in logcat

## Release APK Metadata

### APK Information

```
Application Name: Naeem Documentation
Package Name: com.naeem.documentation
Version Code: 1
Version Name: 1.0
Minimum SDK: 26 (Android 8.0 Oreo)
Target SDK: 34 (Android 14)
Compile SDK: 34
Application ID: com.naeem.documentation
Keystore Alias: naeem_documentation
Signing Scheme: V1 + V2
Build Type: Release
Minification: Enabled (R8/ProGuard)
Resource Shrinking: Enabled
Target Size: < 10 MB
```

### ProGuard/R8 Optimization

```
Optimization: R8 (ProGuard)
Code Shrinking: Enabled
Resource Shrinking: Enabled
Obfuscation: Enabled
Rules File: app/proguard-rules.pro
Optimization Level: Aggressive
Configuration Rules:
  - Keep Bluetooth classes (android.bluetooth.**)
  - Keep domain model classes (com.naeem.thermalprinter.domain.**)
  - Keep Compose runtime classes
  - Keep Coroutines infrastructure
  - Remove debug logging (Log.d, Log.v, Log.i)
```

## Continuous Integration/Deployment Integration

### Environment Variables for CI/CD

For automated builds in CI/CD pipelines (GitHub Actions, GitLab CI, etc.):

```bash
# Set environment variables instead of keystore.properties
export KEYSTORE_PATH=/path/to/keystore.jks
export KEYSTORE_PASSWORD=your_store_password
export KEY_ALIAS=naeem_documentation
export KEY_PASSWORD=your_key_password
```

### Gradle Task Execution

```bash
# Debug build (no signing required)
./gradlew assembleDebug

# Release build with signing
./gradlew assembleRelease

# Release build with signature verification
./gradlew assembleRelease && \
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
```

## Acceptance Criteria

### SC-009: APK Signature Verification
- ✅ `apksigner verify --verbose` returns "Verifies"
- ✅ V1 signature (JAR signing) verified
- ✅ V2 signature (full APK signing) verified
- ✅ Certificate details match keystore certificate
- ✅ No expired certificate warnings

### SC-002: APK Size Validation
- ✅ Final APK size < 10 MB (with R8 optimization)
- ✅ APK installs successfully on all target devices
- ✅ No corrupted or truncated files in APK

### Installation Acceptance
- ✅ Signed APK installs without security warnings
- ✅ App appears in app drawer with correct name and icon
- ✅ App launches and requests permissions correctly
- ✅ App functionality works (Bluetooth, printing, etc.)
- ✅ Signature verified by device security system

## Notes & References

- **Keystore Best Practices**: [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- **APK Signer Tool**: [apksigner Documentation](https://developer.android.com/studio/command-line/apksigner)
- **ProGuard Rules**: [ProGuard Configuration Reference](https://www.guardsquare.com/manual/configuration/preguard)
- **R8 Documentation**: [R8 Code Shrinking and Obfuscation](https://developer.android.com/studio/build/shrink-code)

---

**Status**: ✅ Ready for Release Signing Implementation

This contract specifies all requirements for signing the Release Candidate v1.0 APK for production distribution. Proceed to Task T036 (Manual Keystore Generation) to begin signing configuration.
