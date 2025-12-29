# Release Configuration Data Model - Release Candidate v1.0

**Document Version**: 1.0  
**Date**: 2025-12-29  
**Feature**: Release Candidate v1.0 - Production-Ready Build  
**Status**: Reference Implementation

## Overview

This document defines the data model for Release Candidate v1.0 configuration, including build settings, app identity, signing configuration, and optimization parameters. All entities and their validation rules are specified below.

## Entity Definitions

### ReleaseConfiguration Entity

Represents the complete release build configuration.

```kotlin
data class ReleaseConfiguration(
    val versionCode: Int,           // Unique build number for app updates
    val versionName: String,        // User-facing version string
    val applicationId: String,      // Unique package identifier
    val minSdk: Int,                // Minimum Android API level supported
    val targetSdk: Int,             // Latest Android API level targeted
    val compileSdk: Int,            // SDK version used for compilation
    val appName: String,            // User-visible app name
    val buildType: BuildType,       // Release or Debug
    val namespace: String,          // Kotlin namespace (different from applicationId)
    val javaVersion: JavaVersion    // Compilation target
)

enum class BuildType {
    RELEASE,
    DEBUG
}

enum class JavaVersion {
    VERSION_11  // Target Java 11
}
```

**Validation Rules**:

| Field | Rule | Example |
|-------|------|---------|
| `versionCode` | Must be positive integer, incremented per release | `1` (first release) |
| `versionName` | Must follow semantic versioning (MAJOR.MINOR.PATCH) | `"1.0"`, `"1.1.0"`, `"2.0.0"` |
| `applicationId` | Must be reverse domain notation, lowercase alphanumeric + dots | `"com.naeem.documentation"` |
| `minSdk` | Must be >= 26 (Android 8.0 Oreo) for Bluetooth Classic | `26` |
| `targetSdk` | Must be >= 34 (Android 14), latest stable SDK | `34` |
| `compileSdk` | Must match targetSdk or newer | `34` |
| `appName` | Must be user-facing string, typically 10-30 chars | `"Naeem Documentation"` |
| `buildType` | Must be RELEASE for production APK | `BuildType.RELEASE` |
| `namespace` | Must match package in AndroidManifest.xml | `"com.naeem.thermalprinter"` |
| `javaVersion` | Must be VERSION_11 as per project configuration | `JavaVersion.VERSION_11` |

**Current Release Value**:

```kotlin
val releaseConfiguration = ReleaseConfiguration(
    versionCode = 1,
    versionName = "1.0",
    applicationId = "com.naeem.documentation",
    minSdk = 26,
    targetSdk = 34,
    compileSdk = 34,
    appName = "Naeem Documentation",
    buildType = BuildType.RELEASE,
    namespace = "com.naeem.thermalprinter",
    javaVersion = JavaVersion.VERSION_11
)
```

---

### SigningConfiguration Entity

Represents keystore and signing parameters for release APK signing.

```kotlin
data class SigningConfiguration(
    val keystorePath: String,       // Absolute file path to .jks keystore
    val keystoreAlias: String,      // Key alias within keystore
    val storePassword: String,      // Password protecting keystore
    val keyPassword: String,        // Password protecting the key
    val keyAlgorithm: KeyAlgorithm, // RSA 2048-bit standard
    val validity: Duration,         // Certificate validity period
    val signingSchemes: List<SigningScheme>  // V1 and/or V2
)

enum class KeyAlgorithm {
    RSA_2048,   // Standard: RSA 2048-bit
    RSA_4096    // Enhanced: RSA 4096-bit (optional)
}

enum class SigningScheme {
    V1,  // JAR signing (legacy, all versions)
    V2   // Full APK signing (Android 7.0+)
}
```

**Validation Rules**:

| Field | Rule | Example |
|-------|------|---------|
| `keystorePath` | Must be absolute path, .jks file, outside repo | `/home/user/secure_keys/naeem_release_key.jks` |
| `keystoreAlias` | Must match alias created with keytool | `"naeem_documentation"` |
| `storePassword` | Must be 8+ characters, NOT committed to version control | `"SecurePass123!@#"` |
| `keyPassword` | Must be 8+ characters, can match storePassword | `"SecurePass123!@#"` |
| `keyAlgorithm` | Must be RSA (minimum 2048-bit for release) | `KeyAlgorithm.RSA_2048` |
| `validity` | Certificate validity in days, 9125 days (25 years) recommended | `9125 days` |
| `signingSchemes` | Must include both V1 and V2 for maximum compatibility | `[SigningScheme.V1, SigningScheme.V2]` |

**Current Release Value**:

```kotlin
val signingConfiguration = SigningConfiguration(
    keystorePath = "${System.getProperty("user.home")}/secure_keys/naeem_release_key.jks",
    keystoreAlias = "naeem_documentation",
    storePassword = "[SECURE - LOADED FROM ENV OR KEYSTORE.PROPERTIES]",
    keyPassword = "[SECURE - LOADED FROM ENV OR KEYSTORE.PROPERTIES]",
    keyAlgorithm = KeyAlgorithm.RSA_2048,
    validity = Duration.ofDays(9125),  // 25 years
    signingSchemes = listOf(SigningScheme.V1, SigningScheme.V2)
)
```

**Security Note**: 
- Passwords are **NEVER stored in code** or version control
- Loaded from `keystore.properties` (git-ignored) or environment variables
- For CI/CD: Use GitHub Secrets or equivalent secure storage

---

### OptimizationConfiguration Entity

Represents code shrinking, obfuscation, and resource optimization settings.

```kotlin
data class OptimizationConfiguration(
    val minifyEnabled: Boolean,         // Code shrinking via R8/ProGuard
    val shrinkResources: Boolean,       // Unused resource removal
    val proguardRulesFile: String,      // Custom ProGuard rules
    val proguardConfigFile: String,     // Default ProGuard config
    val obfuscationEnabled: Boolean,    // Class/method name obfuscation
    val optimizationLevel: Int          // R8 optimization aggressiveness
)
```

**Validation Rules**:

| Field | Rule | Example |
|-------|------|---------|
| `minifyEnabled` | Must be `true` for release builds | `true` |
| `shrinkResources` | Must be `true` to reduce APK size | `true` |
| `proguardRulesFile` | Must exist at `app/proguard-rules.pro` | `"app/proguard-rules.pro"` |
| `proguardConfigFile` | Must use Android optimize variant | `"proguard-android-optimize.txt"` |
| `obfuscationEnabled` | Should be `true` for security, except for keep rules | `true` |
| `optimizationLevel` | R8 default = 5 (highest), range 0-5 | `5` |

**Current Release Value**:

```kotlin
val optimizationConfiguration = OptimizationConfiguration(
    minifyEnabled = true,
    shrinkResources = true,
    proguardRulesFile = "app/proguard-rules.pro",
    proguardConfigFile = "proguard-android-optimize.txt",
    obfuscationEnabled = true,
    optimizationLevel = 5  // Maximum optimization
)
```

**ProGuard Rules Summary**:

```properties
# Keep Bluetooth classes (required for connectivity)
-keep class android.bluetooth.** { *; }

# Keep domain model classes (data classes used in serialization)
-keep class com.naeem.thermalprinter.domain.model.** { *; }
-keep class com.naeem.thermalprinter.domain.printer.** { *; }

# Keep Compose runtime (UI framework)
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Keep Coroutines infrastructure
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Remove logging in release (security + size optimization)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
```

---

### AppIdentity Entity

Represents the user-visible app branding and identification.

```kotlin
data class AppIdentity(
    val appName: String,                    // Display name in launcher
    val packageName: String,                // Unique identifier (applicationId)
    val iconResourcePath: String,           // Icon drawable resource
    val launcherIconPath: String,           // Adaptive icon configuration
    val theme: String                       // Material Design theme name
)
```

**Validation Rules**:

| Field | Rule | Example |
|-------|------|---------|
| `appName` | Must match `@string/app_name` in strings.xml | `"Naeem Documentation"` |
| `packageName` | Must be reverse domain, match manifest package | `"com.naeem.documentation"` |
| `iconResourcePath` | Must reference valid drawable resource | `"@drawable/ic_launcher_foreground"` |
| `launcherIconPath` | Must be adaptive icon (API 26+) or legacy mipmap | `"@mipmap/ic_launcher"` |
| `theme` | Must use Material3 Light NoActionBar theme | `"Theme.ThermalPrinter"` |

**Current Release Value**:

```kotlin
val appIdentity = AppIdentity(
    appName = "Naeem Documentation",
    packageName = "com.naeem.documentation",
    iconResourcePath = "@drawable/ic_launcher_foreground",
    launcherIconPath = "@mipmap/ic_launcher",
    theme = "Theme.ThermalPrinter"  // Material3.Light.NoActionBar
)
```

**Icon Design Specification**:

- **Style**: Receipt/printer themed (not default Android robot)
- **Format**: Vector drawable or adaptive icon
- **Foreground**: Receipt icon in white on transparent background
- **Background**: Blue primary color (#0066CC)
- **Monochrome**: Version for single-color display (accessibility)
- **Sizes**: Adaptive icon (API 33+) + legacy fallback for older devices

---

### BuildVerification Entity

Represents build output validation metrics.

```kotlin
data class BuildVerification(
    val buildSuccessful: Boolean,           // Build completed without errors
    val apkPath: String,                    // Output APK file location
    val apkSize: Long,                      // APK file size in bytes
    val apkSizeThreshold: Long = 10 * 1024 * 1024,  // 10 MB limit
    val proguardWarnings: List<String>,     // Non-critical warnings
    val proguardErrors: List<String>,       // Critical errors
    val buildDuration: Duration,            // Build time
    val isSigned: Boolean                   // APK signature verification
)
```

**Validation Rules**:

| Field | Rule | Example |
|-------|------|---------|
| `buildSuccessful` | Must be `true` for release | `true` |
| `apkPath` | Must exist and be readable | `"app/build/outputs/apk/release/app-release.apk"` |
| `apkSize` | Must be < 10 MB | `8_500_000` bytes |
| `proguardWarnings` | Should be minimal, non-critical | Empty list or 0-5 items |
| `proguardErrors` | Must be empty for release | Empty list `[]` |
| `buildDuration` | Should be < 5 minutes on standard machine | `< 300 seconds` |
| `isSigned` | Must be `true` for release APK | `true` |

**Acceptance Criteria**:

| Criteria | Status |
|----------|--------|
| Build completes without errors | ✅ Required |
| APK generated at correct path | ✅ Required |
| APK size < 10 MB | ✅ Required (SC-002) |
| No ProGuard errors | ✅ Required |
| No critical warnings | ✅ Required (SC-010) |
| APK signature valid | ✅ Required (SC-009) |
| Build time < 5 minutes | ✅ Required (SC-001) |

---

## Configuration File Mapping

### build.gradle.kts Mapping

```kotlin
// ReleaseConfiguration
applicationId = "com.naeem.documentation"             // from releaseConfiguration
minSdk = 26                                            // from releaseConfiguration
targetSdk = 34                                         // from releaseConfiguration
compileSdk = 34                                        // from releaseConfiguration
versionCode = 1                                        // from releaseConfiguration
versionName = "1.0"                                    // from releaseConfiguration

// OptimizationConfiguration
isMinifyEnabled = true                                 // from optimizationConfiguration
isShrinkResources = true                               // from optimizationConfiguration
proguardFiles(                                         // from optimizationConfiguration
    getDefaultProguardFile("proguard-android-optimize.txt"),
    "proguard-rules.pro"
)

// SigningConfiguration
signingConfigs.release {                               // from signingConfiguration
    storeFile = keystoreProperties["storeFile"]
    storePassword = keystoreProperties["storePassword"]
    keyAlias = keystoreProperties["keyAlias"]
    keyPassword = keystoreProperties["keyPassword"]
}
```

### AndroidManifest.xml Mapping

```xml
<!-- AppIdentity -->
package="com.naeem.documentation"                      <!-- from appIdentity -->
android:label="@string/app_name"                       <!-- from appIdentity -->
android:icon="@mipmap/ic_launcher"                     <!-- from appIdentity -->
android:theme="@style/Theme.ThermalPrinter"            <!-- from appIdentity -->
```

### strings.xml Mapping

```xml
<!-- AppIdentity -->
<string name="app_name">Naeem Documentation</string>   <!-- from appIdentity -->
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2025-12-29 | Initial release configuration model |

---

## Relationships & Dependencies

```
ReleaseConfiguration
    ├── AppIdentity (branding)
    ├── SigningConfiguration (for APK signing)
    ├── OptimizationConfiguration (code shrinking)
    └── BuildVerification (validation results)
```

- **Release** depends on all configurations being valid before build starts
- **Signing** depends on Keystore being available and Release config validated
- **Optimization** rules must preserve Bluetooth and domain classes (see ProGuard rules)
- **Verification** validates all other entities after build completion

---

## Compliance & Standards

- **Android SDK**: Targets API 26-34 (supported 8 years of Android versions)
- **Java**: Compiled to Java 11 bytecode (widely compatible)
- **Kotlin**: 1.9.10 (recent stable version)
- **Compose**: Material 3 with Jetpack Compose (modern Android UI framework)
- **Security**: RSA 2048-bit signing (industry standard for app distribution)

---

**Status**: ✅ Ready for Reference

This data model is complete and all entities are properly defined with validation rules. Use this model to verify release configuration compliance during build validation.
