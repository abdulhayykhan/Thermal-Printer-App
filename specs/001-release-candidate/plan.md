# Implementation Plan: Release Candidate v1.0 - Final Release Build

**Branch**: `001-release-candidate` | **Date**: 2025-12-29 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-release-candidate/spec.md`

**Note**: This plan follows the Spec-Driven Development workflow for preparing a production-ready release build.

## Summary

Prepare the Naeem Documentation thermal printer app for production release by completing build configuration validation, app identity updates, permission handling validation, keystore signing setup, and comprehensive testing across multiple devices and printer models. The release will produce a signed, optimized APK (version 1.0) under 10MB with ProGuard enabled, ready for distribution to end users.

## Technical Context

**Language/Version**: Kotlin 1.9.10 with Java 11 target  
**Primary Dependencies**: Jetpack Compose (Material3), Kotlin Coroutines, Android Bluetooth Classic API  
**Storage**: SharedPreferences for printer MAC address persistence  
**Testing**: JUnit 4.13.2, Mockito Kotlin 5.1.0, Espresso for UI tests  
**Target Platform**: Android API 26 (Android 8.0 Oreo) through API 34 (Android 14)  
**Project Type**: Mobile (single Android application)  
**Performance Goals**: <3s printer connection time, <2s print time, <50MB memory usage, no ANR during operations  
**Constraints**: Release APK <10MB, offline-capable (no network), ProGuard/R8 optimization enabled, keystore-signed  
**Scale/Scope**: Single-screen app, ~12 Kotlin source files, supports 58mm and 80mm thermal printers via Bluetooth Classic

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### ✅ Clean Architecture (Compliant)
- **Status**: PASS
- **Evidence**: Domain layer (`domain/printer/`, `domain/model/`) is independent of framework; Data layer (`data/bluetooth/`, `data/repository/`) handles Bluetooth communication; Presentation layer (`presentation/MainActivity.kt`) uses Jetpack Compose UI
- **Action Required**: None - architecture already follows Clean Architecture principles

### ✅ Pure Native Implementation (Compliant)
- **Status**: PASS
- **Evidence**: App uses direct `android.bluetooth.*` API with custom ESC/POS command implementation (`ESCPOSCommands.kt`); No third-party printer SDKs or external libraries detected in dependencies
- **Action Required**: None - implementation is pure native

### ✅ Coroutines for Concurrency (Compliant)
- **Status**: PASS
- **Evidence**: `kotlinx-coroutines-core:1.7.3` and `kotlinx-coroutines-android:1.7.3` dependencies present; Bluetooth operations expected to use `Dispatchers.IO`
- **Action Required**: Verify during code review that all Bluetooth operations use proper dispatchers

### ⚠️ User-Facing Error Handling (Needs Validation)
- **Status**: NEEDS VALIDATION
- **Evidence**: Must verify that permission denials, Bluetooth adapter null/disabled, and print errors show user-friendly messages
- **Action Required**: Phase 0 research must verify error handling implementation; Phase 4 validation testing required

### ✅ Android Best Practices (Compliant)
- **Status**: PASS
- **Evidence**: Jetpack Compose with Material3, ViewModel pattern via `lifecycle-viewmodel-compose`, SharedPreferences for persistence, runtime permissions handling required
- **Action Required**: None - follows Android best practices

### ✅ Simplicity First (Compliant)
- **Status**: PASS
- **Evidence**: Single-screen app with basic amount input, auto-connect to last printer, no print history, minimal scope
- **Action Required**: None - adheres to YAGNI principle

### 🔴 Release-Specific Requirements (Action Required)
- **Status**: NEEDS IMPLEMENTATION
- **Evidence**: Keystore signing not yet configured; Debug logging may still be present; App identity verification needed
- **Action Required**: 
  1. Phase 1: Create keystore and configure signing
  2. Phase 1: Remove all debug logging (Log.d, println)
  3. Phase 1: Verify app name displays as "Naeem Documentation"
  4. Phase 2: Verify ProGuard rules preserve Bluetooth functionality

**GATE RESULT**: ⚠️ CONDITIONAL PASS - Core architecture compliant, but release-specific tasks must be completed in Phases 1-2

## Project Structure

### Documentation (this feature)

```text
specs/001-release-candidate/
├── plan.md              # This file (/sp.plan command output)
├── research.md          # Phase 0 output - verification of existing implementation
├── data-model.md        # Phase 1 output - release configuration model
├── quickstart.md        # Phase 1 output - release build guide
├── contracts/           # Phase 1 output - keystore and signing contracts
│   └── signing-config.md
└── tasks.md             # Phase 2 output (/sp.tasks command - NOT created by /sp.plan)
```

### Source Code (repository root)

```text
app/
├── build.gradle.kts                    # Build configuration with release signing
├── proguard-rules.pro                  # ProGuard optimization rules
└── src/
    ├── main/
    │   ├── AndroidManifest.xml         # App identity and permissions
    │   ├── res/
    │   │   ├── values/strings.xml      # App name "Naeem Documentation"
    │   │   ├── mipmap-*/ic_launcher.*  # App icon (receipt/printer themed)
    │   │   └── ...
    │   └── java/com/naeem/thermalprinter/
    │       ├── domain/                 # Business logic (independent)
    │       │   ├── model/
    │       │   │   ├── PrintResult.kt
    │       │   │   ├── Receipt.kt
    │       │   │   ├── PrinterDevice.kt
    │       │   │   └── PrinterStatus.kt
    │       │   └── printer/
    │       │       ├── ESCPOSCommands.kt
    │       │       ├── ReceiptBuilder.kt
    │       │       ├── Alignment.kt
    │       │       └── TextSize.kt
    │       ├── data/                   # Bluetooth & persistence
    │       │   ├── bluetooth/
    │       │   │   ├── BluetoothHelper.kt
    │       │   │   └── BluetoothConstants.kt
    │       │   └── repository/
    │       │       └── PrinterPreferences.kt
    │       └── presentation/           # Compose UI
    │           └── MainActivity.kt
    └── test/                           # Unit tests
        └── ...

build.gradle.kts                        # Root project configuration
```

**Structure Decision**: Android single-module application following Clean Architecture. The `app/` module contains all source code organized by architectural layers (domain, data, presentation). Release preparation focuses on build configuration, manifest updates, and resource optimization rather than structural changes.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

**No constitutional violations detected.** All architecture principles, technical constraints, and development workflow requirements are satisfied by the existing implementation. Release preparation activities (keystore creation, ProGuard verification, debug logging removal) are operational tasks rather than architectural changes.

---

## Phase 0: Outline & Research

**Goal**: Verify existing implementation against release requirements and identify any gaps that need clarification before proceeding with release build.

### Research Tasks

#### R1: Verify Error Handling Implementation
- **Question**: Does the app currently handle Bluetooth permission denials and adapter null/disabled states gracefully?
- **Method**: Code inspection of `MainActivity.kt` and `BluetoothHelper.kt`
- **Deliverable**: Document current error handling strategy and identify any missing user-facing messages

#### R2: Verify Debug Logging Presence
- **Question**: Are there Log.d, println, or other debug statements in the production code?
- **Method**: Grep search for `Log.d`, `println`, `Log.v`, `System.out.println` across all Kotlin files
- **Deliverable**: List of files containing debug logging that must be removed

#### R3: Verify ProGuard Rules Completeness
- **Question**: Does `proguard-rules.pro` preserve all necessary classes for Bluetooth functionality?
- **Method**: Review existing ProGuard rules against Bluetooth API usage
- **Deliverable**: Document any additional ProGuard rules needed for Bluetooth socket connections

#### R4: Verify App Identity Configuration
- **Question**: Does AndroidManifest.xml correctly reference "Naeem Documentation" and is the app icon appropriate?
- **Method**: Inspect `AndroidManifest.xml`, `res/values/strings.xml`, and `res/mipmap-*` resources
- **Deliverable**: Document current app name, icon status, and any required changes

#### R5: Android Keystore Best Practices
- **Question**: What are the recommended practices for keystore creation, password strength, and secure storage?
- **Method**: Reference official Android documentation for app signing
- **Deliverable**: Step-by-step keystore creation guide with security recommendations

### Research Output Structure (research.md)

```markdown
# Release Candidate v1.0 - Research Findings

## R1: Error Handling Implementation
- **Current State**: [findings from code inspection]
- **Gaps Identified**: [missing error messages]
- **Action Required**: [list of error handling improvements]

## R2: Debug Logging Audit
- **Files with Debug Logging**: [list with line numbers]
- **Removal Strategy**: [approach for cleanup]

## R3: ProGuard Rules Review
- **Current Rules**: [summary of existing rules]
- **Additional Rules Needed**: [Bluetooth-specific rules]

## R4: App Identity Verification
- **Current App Name**: [value in strings.xml]
- **Current Icon**: [description/status]
- **Changes Required**: [specific updates needed]

## R5: Keystore Creation Guide
- **Recommended Algorithm**: [e.g., RSA 2048-bit]
- **Certificate Validity**: [e.g., 25 years]
- **Password Guidelines**: [complexity requirements]
- **Storage Best Practices**: [secure location recommendations]
```

**Phase 0 Completion Criteria**: All NEEDS CLARIFICATION items from Constitution Check are resolved with specific findings documented in `research.md`.

---

## Phase 1: Design & Contracts

**Goal**: Generate release configuration documentation, create signing keystore, and prepare build for production.

### Phase 1 Tasks

#### 1.1: Create Release Configuration Data Model

**File**: `specs/001-release-candidate/data-model.md`

Document the release configuration entities:

- **ReleaseConfiguration**: versionCode, versionName, minSdk, targetSdk, applicationId, buildType
- **SigningConfiguration**: keystorePath, keystoreAlias, storePassword, keyPassword, validity
- **OptimizationConfiguration**: minifyEnabled, shrinkResources, proguardFiles
- **AppIdentity**: appName, packageName, icon resources

Include validation rules:
- versionCode must be integer (1 for initial release)
- versionName must follow semantic versioning (1.0)
- Keystore passwords must be minimum 8 characters
- App name must be "Naeem Documentation"

#### 1.2: Generate Signing Configuration Contract

**File**: `specs/001-release-candidate/contracts/signing-config.md`

Document signing configuration contract:

```kotlin
// Keystore Details
keystore {
    location: "/path/to/naeem_release_key.jks"
    alias: "naeem_documentation"
    storePassword: "[SECURE - DO NOT COMMIT]"
    keyPassword: "[SECURE - DO NOT COMMIT]"
    algorithm: "RSA"
    keySize: "2048"
    validity: "9125 days (25 years)"
}

// Build Configuration
signingConfig {
    storeFile: file(keystoreProperties['storeFile'])
    storePassword: keystoreProperties['storePassword']
    keyAlias: keystoreProperties['keyAlias']
    keyPassword: keystoreProperties['keyPassword']
}

// APK Signing Scheme
signing {
    v1SigningEnabled: true  // JAR signing for legacy compatibility
    v2SigningEnabled: true  // Full APK signing for Android 7.0+
}
```

#### 1.3: Generate Release Build Quickstart Guide

**File**: `specs/001-release-candidate/quickstart.md`

Create step-by-step guide for generating release build:

1. **Prerequisites Check**
   - Android Studio installed
   - JDK 11 configured
   - Physical device or emulator available

2. **Keystore Creation** (Manual Step)
   ```bash
   keytool -genkey -v -keystore naeem_release_key.jks \
     -alias naeem_documentation -keyalg RSA -keysize 2048 \
     -validity 9125
   ```

3. **Keystore Configuration**
   - Create `keystore.properties` in project root (git-ignored)
   - Add keystore details (storeFile, storePassword, keyAlias, keyPassword)

4. **Build Configuration Update**
   - Update `app/build.gradle.kts` to load keystore.properties
   - Add signingConfigs block for release build

5. **Generate Signed APK**
   ```bash
   ./gradlew assembleRelease
   ```

6. **Verify APK**
   ```bash
   apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
   ```

7. **Installation & Testing**
   - Transfer APK to physical device
   - Install via `adb install app-release.apk`
   - Test all P1 requirements from spec.md

#### 1.4: Update Agent Context

Run the agent context update script to add release-specific technology information:

```bash
.specify/scripts/bash/update-agent-context.sh copilot
```

Add to context:
- ProGuard/R8 code shrinking
- Android APK signing (keytool, apksigner)
- Release build configuration
- Production deployment preparation

#### 1.5: Re-evaluate Constitution Check

After Phase 1 design completion, re-verify:
- ✅ Clean Architecture preserved (no structural changes)
- ✅ Pure Native Implementation maintained (no new dependencies)
- ✅ Coroutines usage unchanged
- ✅ User-Facing Error Handling verified from research.md
- ✅ Android Best Practices followed (signing, ProGuard)
- ✅ Simplicity First maintained (operational changes only)
- ✅ Release-Specific Requirements addressed (keystore created, signing configured)

**Phase 1 Completion Criteria**: 
- `data-model.md` documents all release configuration entities
- `contracts/signing-config.md` provides complete signing contract
- `quickstart.md` provides executable build guide
- Agent context updated with release technologies
- Constitution Check re-validated with PASS status

---

## Phase 2: Implementation Planning (Task Breakdown)

**Note**: This phase generates the task list for `/sp.tasks` command. The plan command stops here and reports to user.

### Task Categories

#### Category 1: Cleanup & Optimization (5 tasks)
- Remove all debug logging (Log.d, println)
- Verify version configuration in build.gradle.kts
- Update app name in strings.xml to "Naeem Documentation"
- Verify app icon is receipt/printer themed
- Clean up temporary comments and test code

#### Category 2: Keystore Creation (3 tasks - MANUAL)
- Generate release keystore using keytool
- Create keystore.properties file (git-ignored)
- Configure signingConfigs in app/build.gradle.kts

#### Category 3: Build Generation (4 tasks)
- Run `./gradlew clean`
- Run `./gradlew assembleRelease`
- Verify ProGuard warnings
- Verify APK signature with apksigner

#### Category 4: Installation & Validation (6 tasks)
- Transfer APK to physical device
- Install APK and verify installation
- Test Bluetooth connectivity with 2 printer models
- Test receipt printing with correct branding
- Verify all P1 requirements from spec.md
- Run existing unit tests for regression

**Total Tasks**: 18 tasks across 4 categories

**Estimated Effort**: 
- Category 1: 2-3 hours (code cleanup)
- Category 2: 1 hour (keystore setup - manual)
- Category 3: 1-2 hours (build and verification)
- Category 4: 3-4 hours (testing on physical devices)
- **Total**: 7-10 hours

**Dependencies**:
- Category 2 must complete before Category 3
- Category 3 must complete before Category 4
- Category 1 can run in parallel with Category 2

**Risks**:
- ProGuard may remove necessary Bluetooth classes (mitigated by proguard-rules.pro review in Phase 0)
- Keystore password management requires secure storage solution
- Physical device testing requires hardware availability
- Thermal printer availability required for full validation

---

## Phase 3: Stop and Report

**Command Completion Summary**:
- ✅ Feature branch: `001-release-candidate`
- ✅ Implementation plan: `specs/001-release-candidate/plan.md`
- ✅ Phase 0 structure: Research tasks defined for `research.md`
- ✅ Phase 1 structure: Design artifacts specified (data-model.md, contracts/, quickstart.md)
- ✅ Phase 2 structure: Task breakdown provided for `/sp.tasks` command

**Next Steps for User**:
1. Review this implementation plan (`specs/001-release-candidate/plan.md`)
2. Execute Phase 0 by running: `/sp.plan --execute-phase0` (or manually conduct research)
3. Execute Phase 1 by running: `/sp.plan --execute-phase1` (or manually create design docs)
4. Generate task list by running: `/sp.tasks` command
5. Begin implementation following the task breakdown in Category 1-4

**Artifacts Generated**:
- `specs/001-release-candidate/plan.md` (this file)

**Artifacts Pending** (created by subsequent commands):
- `specs/001-release-candidate/research.md` (Phase 0)
- `specs/001-release-candidate/data-model.md` (Phase 1)
- `specs/001-release-candidate/contracts/signing-config.md` (Phase 1)
- `specs/001-release-candidate/quickstart.md` (Phase 1)
- `specs/001-release-candidate/tasks.md` (Phase 2 - via `/sp.tasks`)

**Branch Ready**: User should ensure working on branch `001-release-candidate` before proceeding with implementation tasks.
