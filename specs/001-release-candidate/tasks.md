---
description: "Release Candidate v1.0 - Production-Ready Build Task List"
---

# Tasks: Release Candidate v1.0 - Final Release Build

**Input**: Design documents from `/specs/001-release-candidate/`
**Prerequisites**: plan.md, spec.md

**Organization**: Tasks are grouped by user story (US1-US7) to enable independent validation and testing of each release requirement.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Android project: `app/build.gradle.kts`, `app/src/main/`, `app/proguard-rules.pro`

---

## Phase 1: Setup (Environment & Prerequisites)

**Purpose**: Verify development environment and prepare for release build tasks

- [x] T001 Verify Android Studio installed with SDK 34 and build tools configured
- [x] T002 Verify JDK 11 is configured and accessible via `java -version`
- [x] T003 Verify at least 2 physical Android devices (API 26+) available for testing
- [x] T004 Verify at least 2 thermal printer models (58mm and 80mm) paired and available
- [x] T005 Create backup of current codebase before beginning release modifications

**Estimated Effort**: 30 minutes (manual verification)

---

## Phase 2: Foundational (Pre-Release Code Audit)

**Purpose**: Audit and clean codebase before release build configuration

**⚠️ CRITICAL**: These tasks must complete before any user story implementation begins

- [x] T006 Audit all Kotlin files for debug logging with `grep -r "Log\.d\|Log\.v\|println" app/src/main/java/`
- [x] T007 Audit all Kotlin files for temporary test code or TODO comments with `grep -r "TODO\|FIXME\|TEST" app/src/main/java/`
- [x] T008 Review ProGuard rules in app/proguard-rules.pro for Bluetooth class preservation
- [x] T009 Verify app/src/main/res/values/strings.xml contains app_name = "Naeem Documentation"
- [x] T010 Verify app/src/main/res/mipmap-* directories contain appropriate launcher icon (not default Android robot)

**Estimated Effort**: 1 hour (code inspection)

**Checkpoint**: Codebase audit complete - ready for user story implementation

---

## Phase 3: User Story 1 - Build Configuration Validation (Priority: P1) 🎯 RELEASE BLOCKER

**Goal**: Verify production build configuration is properly set up for optimized, secure release APK

**Independent Test**: Run `./gradlew assembleRelease` and verify APK is generated with size < 10MB

### Implementation for User Story 1

- [x] T011 [US1] Verify minSdk=26, targetSdk=34, versionCode=1, versionName="1.0" in app/build.gradle.kts
- [x] T012 [US1] Verify applicationId="com.naeem.documentation" in app/build.gradle.kts defaultConfig
- [x] T013 [US1] Verify isMinifyEnabled=true and isShrinkResources=true in release buildType in app/build.gradle.kts
- [x] T014 [US1] Verify proguardFiles includes "proguard-android-optimize.txt" and "proguard-rules.pro" in app/build.gradle.kts
- [ ] T015 [US1] Run `./gradlew clean` to clear previous build artifacts
- [ ] T016 [US1] Run `./gradlew assembleRelease --info` to generate release APK with detailed output
- [ ] T017 [US1] Verify APK exists at app/build/outputs/apk/release/app-release.apk
- [ ] T018 [US1] Check APK size with `ls -lh app/build/outputs/apk/release/app-release.apk` (must be < 10MB)
- [ ] T019 [US1] Review ProGuard warnings in build output for critical Bluetooth or app functionality issues

**Estimated Effort**: 1 hour (automated verification)

**Acceptance Criteria**:
- ✅ Build completes successfully with minification enabled
- ✅ APK size < 10MB
- ✅ No critical ProGuard warnings for Bluetooth functionality

**Checkpoint**: Build configuration validated - release APK can be generated

---

## Phase 4: User Story 2 - App Identity Updates (Priority: P1) 🎯 RELEASE BLOCKER

**Goal**: Ensure correct app name "Naeem Documentation" and appropriate branding visible to users

**Independent Test**: Install APK on device and verify app drawer shows "Naeem Documentation" with receipt/printer icon

### Implementation for User Story 2

- [x] T020 [US2] Verify android:label="@string/app_name" in app/src/main/AndroidManifest.xml
- [x] T021 [US2] Verify app_name="Naeem Documentation" in app/src/main/res/values/strings.xml
- [x] T022 [US2] Verify package="com.naeem.documentation" in app/src/main/AndroidManifest.xml matches applicationId
- [x] T023 [US2] Review app icon in app/src/main/res/mipmap-* directories (should be receipt/printer themed, not Android robot)
- [x] T024 [US2] If icon is default Android robot, replace with receipt/printer themed icon in all density folders (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- [ ] T025 [US2] Rebuild APK with `./gradlew assembleRelease` after any identity changes
- [ ] T026 [US2] Install APK on test device via `adb install -r app/build/outputs/apk/release/app-release.apk`
- [ ] T027 [US2] Verify app drawer displays "Naeem Documentation" with correct icon

**Estimated Effort**: 45 minutes (manual verification + icon replacement if needed)

**Acceptance Criteria**:
- ✅ App name displays as "Naeem Documentation" in app drawer
- ✅ Package name is com.naeem.documentation
- ✅ Launcher icon is receipt/printer themed (not default Android robot)

**Checkpoint**: App identity correct - users can identify the app by name and icon

---

## Phase 5: User Story 3 - Permission Handling Validation (Priority: P1) 🎯 RELEASE BLOCKER

**Goal**: Verify app handles permission denials gracefully without crashing

**Independent Test**: Install app, deny Bluetooth permissions, verify "Permission Required" message shown instead of crash

### Implementation for User Story 3

- [ ] T028 [US3] Review permission handling code in app/src/main/java/com/naeem/thermalprinter/presentation/MainActivity.kt
- [ ] T029 [US3] Verify Bluetooth permission denial shows toast "Bluetooth permission is required for printer connection"
- [ ] T030 [US3] Verify null or disabled Bluetooth adapter shows toast "Please enable Bluetooth to use this app"
- [ ] T031 [US3] Remove any debug logging from permission handling code in MainActivity.kt
- [ ] T032 [US3] Build and install APK: `./gradlew assembleRelease && adb install -r app/build/outputs/apk/release/app-release.apk`
- [ ] T033 [US3] Test: Launch app, deny Bluetooth CONNECT permission when prompted, verify no crash and toast message displayed
- [ ] T034 [US3] Test: Disable Bluetooth in device settings, launch app, verify "Please enable Bluetooth" toast displayed
- [ ] T035 [US3] Test: Attempt printer connection without permissions, verify "Bluetooth permission denied" error message

**Estimated Effort**: 1.5 hours (manual testing on physical device)

**Acceptance Criteria**:
- ✅ App does not crash when Bluetooth permissions denied
- ✅ Appropriate toast messages displayed for permission denial and disabled Bluetooth
- ✅ User receives clear guidance when permissions are missing

**Checkpoint**: Permission handling robust - app won't crash on permission denial

---

## Phase 6: User Story 7 - Signing Configuration (Priority: P1) 🎯 RELEASE BLOCKER

**Goal**: Configure keystore signing so release APK can be distributed and installed on user devices

**Independent Test**: Generate signed APK and verify signature via `apksigner verify`

### Manual Keystore Creation (User-Executed)

**⚠️ MANUAL TASK**: User must execute these commands before proceeding with signing configuration tasks

```bash
# Step 1: Generate release keystore (MANUAL - execute in terminal)
keytool -genkey -v -keystore naeem_release_key.jks \
  -alias naeem_documentation -keyalg RSA -keysize 2048 \
  -validity 9125

# Follow prompts to set passwords and certificate details
# - Store password: Choose strong password (minimum 8 characters)
# - Key password: Choose strong password (minimum 8 characters)
# - CN: Your name or organization
# - OU: Organizational unit (e.g., Development)
# - O: Organization name (e.g., Naeem Documentation)
# - L: City
# - ST: State
# - C: Country code (2 letters)

# Step 2: Move keystore to secure location outside repository
mv naeem_release_key.jks ~/secure_keys/

# Step 3: Create keystore.properties file (MANUAL - git-ignored)
cat > keystore.properties <<EOF
storeFile=/home/[username]/secure_keys/naeem_release_key.jks
storePassword=[YOUR_STORE_PASSWORD]
keyAlias=naeem_documentation
keyPassword=[YOUR_KEY_PASSWORD]
EOF

# Step 4: Add keystore.properties to .gitignore if not already present
echo "keystore.properties" >> .gitignore
```

### Implementation for User Story 7

- [ ] T036 [US7] MANUAL: Execute keystore generation commands above to create naeem_release_key.jks
- [ ] T037 [US7] MANUAL: Create keystore.properties file in project root with keystore details (DO NOT COMMIT)
- [ ] T038 [US7] Verify keystore.properties is listed in .gitignore file
- [ ] T039 [US7] Update app/build.gradle.kts to load keystore.properties in signingConfigs block (before android block)
- [ ] T040 [US7] Add signingConfig to release buildType referencing signingConfigs.release in app/build.gradle.kts
- [ ] T041 [US7] Run `./gradlew clean assembleRelease` to generate signed APK
- [ ] T042 [US7] Verify signed APK signature with `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- [ ] T043 [US7] Test installation on fresh device to verify signature: `adb uninstall com.naeem.documentation && adb install app/build/outputs/apk/release/app-release.apk`

**Estimated Effort**: 1.5 hours (manual keystore creation + configuration + verification)

**Acceptance Criteria**:
- ✅ Keystore generated and stored securely outside repository
- ✅ Release APK signed with keystore
- ✅ Signature verified via apksigner
- ✅ APK installs successfully without security warnings

**Checkpoint**: Signing configuration complete - APK ready for distribution

---

## Phase 7: User Story 4 - Bluetooth Connectivity Validation (Priority: P2)

**Goal**: Verify app connects to and prints on multiple thermal printer models reliably

**Independent Test**: Pair 2 different thermal printer models, connect to each, print test receipts on both

### Implementation for User Story 4

- [ ] T044 [P] [US4] Pair first thermal printer (58mm) with test device via Bluetooth settings
- [ ] T045 [P] [US4] Pair second thermal printer (80mm) with test device via Bluetooth settings
- [ ] T046 [US4] Install signed APK on test device: `adb install -r app/build/outputs/apk/release/app-release.apk`
- [ ] T047 [US4] Test: Open app, grant Bluetooth permissions, select first printer (58mm)
- [ ] T048 [US4] Verify connection status displays "Connected: [Printer Name]" for first printer
- [ ] T049 [US4] Test: Enter amount "100.00", tap PRINT, verify receipt prints with correct formatting
- [ ] T050 [US4] Verify printed receipt contains: business name, date, amount, thank you message
- [ ] T051 [US4] Test: Disconnect first printer, select second printer (80mm) in app
- [ ] T052 [US4] Verify connection status displays "Connected: [Printer Name]" for second printer
- [ ] T053 [US4] Test: Enter amount "250.50", tap PRINT, verify receipt prints on second printer
- [ ] T054 [US4] Test: Turn off printer, attempt connection, verify "Failed to connect to printer" error message displayed
- [ ] T055 [US4] Document printer models tested and results in test notes

**Estimated Effort**: 2 hours (manual hardware testing with 2 printers)

**Acceptance Criteria**:
- ✅ App successfully connects to 2 different printer models
- ✅ Receipts print with correct formatting on both printers
- ✅ Connection failures display clear error messages
- ✅ No crashes during printer operations

**Checkpoint**: Bluetooth connectivity validated across multiple printer models

---

## Phase 8: User Story 5 - Device Rotation Handling (Priority: P2)

**Goal**: Verify app handles screen rotation gracefully without losing input or crashing

**Independent Test**: Enter amount, rotate device, verify amount preserved and UI renders correctly

### Implementation for User Story 5

- [ ] T056 [US5] Install signed APK on test device: `adb install -r app/build/outputs/apk/release/app-release.apk`
- [ ] T057 [US5] Test: Launch app, enter amount "150.75" in input field
- [ ] T058 [US5] Test: Rotate device from portrait to landscape
- [ ] T059 [US5] Verify entered amount "150.75" is preserved and displayed correctly after rotation
- [ ] T060 [US5] Test: Connect to printer, verify connection status displayed
- [ ] T061 [US5] Test: Rotate device while connected to printer
- [ ] T062 [US5] Verify printer connection status maintained and displayed correctly after rotation
- [ ] T063 [US5] Test: Open printer selection, rotate device
- [ ] T064 [US5] Verify printer selection UI recreates without crashing

**Estimated Effort**: 45 minutes (manual rotation testing)

**Acceptance Criteria**:
- ✅ User input preserved during rotation
- ✅ Connection status maintained during rotation
- ✅ No crashes during screen orientation changes

**Checkpoint**: Screen rotation handling validated

---

## Phase 9: User Story 6 - Memory and Performance Validation (Priority: P3)

**Goal**: Verify app runs smoothly without memory leaks during extended use

**Independent Test**: Run app for 30+ minutes, perform multiple operations, monitor memory via Android Studio Profiler

### Implementation for User Story 6

- [ ] T065 [US6] Connect test device to Android Studio with USB debugging enabled
- [ ] T066 [US6] Launch Android Studio Profiler and attach to com.naeem.documentation process
- [ ] T067 [US6] Test: Run app for 30 minutes with continuous monitoring
- [ ] T068 [US6] Test: Perform 20+ print operations during 30-minute session
- [ ] T069 [US6] Test: Connect and disconnect printers multiple times (10+ cycles)
- [ ] T070 [US6] Monitor memory allocation graph in Profiler for memory leaks
- [ ] T071 [US6] Verify no significant memory growth (< 20MB increase over 30 minutes)
- [ ] T072 [US6] Verify app remains responsive after extended use (button taps respond in < 1 second)
- [ ] T073 [US6] Capture memory profiler screenshot showing stable memory usage
- [ ] T074 [US6] Document findings: max memory usage, leak detection results, responsiveness

**Estimated Effort**: 2 hours (profiler setup + extended testing + analysis)

**Acceptance Criteria**:
- ✅ No significant memory leaks detected during 30-minute session
- ✅ App remains responsive after 20+ print operations
- ✅ Bluetooth connections properly closed and resources released

**Checkpoint**: Memory performance validated for production use

---

## Phase 10: Final Validation & Release Preparation

**Purpose**: Comprehensive testing and final checks before production release

- [ ] T075 Test on API 26 device (Android 8.0 Oreo - minimum SDK) - install, connect, print
- [ ] T076 Test on API 34 device (Android 14 - target SDK) - install, connect, print
- [ ] T077 Test edge case: Bluetooth enabled during runtime after initially disabled
- [ ] T078 Test edge case: Printer runs out of paper during printing
- [ ] T079 Test edge case: Enter extremely large amount (999999.99 SR) and print
- [ ] T080 Test edge case: Multiple Bluetooth devices paired but only one is printer
- [ ] T081 Run existing unit tests: `./gradlew test --info`
- [ ] T082 Verify all unit tests pass with 100% success rate
- [ ] T083 Generate final signed release APK: `./gradlew clean assembleRelease`
- [ ] T084 Verify final APK signature: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- [ ] T085 Document APK details: version, size, build date, signature SHA-256
- [ ] T086 Create release notes documenting all validated features and tested devices/printers
- [ ] T087 Archive release APK in secure location with version tag
- [ ] T088 Update project README with installation instructions for end users

**Estimated Effort**: 3 hours (comprehensive edge case testing + documentation)

**Acceptance Criteria**:
- ✅ All P1 requirements validated (US1, US2, US3, US7)
- ✅ All P2 requirements validated (US4, US5)
- ✅ All unit tests passing
- ✅ Edge cases handled gracefully
- ✅ Release APK signed and ready for distribution

**Checkpoint**: Release Candidate v1.0 validated and ready for production deployment

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories P1 (Phases 3-6)**: All depend on Foundational phase completion - RELEASE BLOCKERS
  - US1: Build Configuration Validation (independent)
  - US2: App Identity Updates (independent, but rebuild after US1)
  - US3: Permission Handling Validation (independent, requires built APK from US1)
  - US7: Signing Configuration (depends on US1 build configuration validated)
- **User Stories P2 (Phases 7-8)**: Can proceed after P1 user stories complete
  - US4: Bluetooth Connectivity Validation (depends on US7 signed APK)
  - US5: Device Rotation Handling (depends on US7 signed APK)
- **User Stories P3 (Phase 9)**: Can proceed after P2 validation
  - US6: Memory and Performance Validation (requires fully functional app from US4)
- **Final Validation (Phase 10)**: Depends on all user stories complete

### User Story Dependencies

- **User Story 1 (P1)**: Independent - validates build configuration
- **User Story 2 (P1)**: Uses US1 build configuration
- **User Story 3 (P1)**: Requires APK from US1 for testing
- **User Story 7 (P1)**: Requires US1 validated before adding signing
- **User Story 4 (P2)**: Requires signed APK from US7
- **User Story 5 (P2)**: Requires signed APK from US7
- **User Story 6 (P3)**: Requires fully functional app from US4

### Critical Path (Release Blockers)

```
Setup (Phase 1)
  ↓
Foundational (Phase 2) - Code Audit
  ↓
US1 (Phase 3) - Build Configuration ← BLOCKER
  ↓
US2 (Phase 4) - App Identity ← BLOCKER
  ↓
US3 (Phase 5) - Permission Handling ← BLOCKER
  ↓
US7 (Phase 6) - Signing Configuration ← BLOCKER
  ↓
US4 (Phase 7) - Bluetooth Connectivity
  ↓
US5 (Phase 8) - Device Rotation
  ↓
US6 (Phase 9) - Memory Performance
  ↓
Final Validation (Phase 10)
```

### Parallel Opportunities

- Phase 2 (T006-T010): All foundational audit tasks can run in parallel
- Phase 7 (T044-T045): Printer pairing can happen in parallel
- Limited parallelization due to sequential build → sign → test workflow

### Manual vs Automated Tasks

**Manual Tasks** (require user intervention):
- T001-T005: Environment verification
- T036-T037: Keystore generation and properties file creation
- T044-T045: Physical printer pairing
- T047-T055: Hardware testing with printers
- T056-T064: Physical device rotation testing
- T075-T080: Edge case testing on physical devices

**Automated Tasks** (can be scripted):
- T006-T010: Code auditing with grep
- T011-T019: Build configuration verification and APK generation
- T020-T027: App identity verification (except icon replacement)
- T032: APK build and install
- T041-T042: Signed APK generation and verification
- T081-T082: Unit test execution

---

## Task Summary

**Total Tasks**: 88 tasks

### Tasks by User Story
- Setup (Phase 1): 5 tasks
- Foundational (Phase 2): 5 tasks
- US1 - Build Configuration (Phase 3): 9 tasks
- US2 - App Identity (Phase 4): 8 tasks
- US3 - Permission Handling (Phase 5): 8 tasks
- US7 - Signing Configuration (Phase 6): 8 tasks
- US4 - Bluetooth Connectivity (Phase 7): 12 tasks
- US5 - Device Rotation (Phase 8): 9 tasks
- US6 - Memory Performance (Phase 9): 10 tasks
- Final Validation (Phase 10): 14 tasks

### By Priority
- **P1 (Release Blockers)**: 38 tasks (US1, US2, US3, US7)
- **P2 (High Priority)**: 21 tasks (US4, US5)
- **P3 (Performance)**: 10 tasks (US6)
- **Setup/Foundation**: 10 tasks
- **Final Validation**: 14 tasks

### By Task Type
- **Verification/Audit**: 25 tasks
- **Build/Deploy**: 15 tasks
- **Testing (Manual)**: 35 tasks
- **Configuration**: 8 tasks
- **Documentation**: 5 tasks

### Estimated Total Effort
- Setup & Foundation: 1.5 hours
- P1 Tasks (US1, US2, US3, US7): 4.75 hours
- P2 Tasks (US4, US5): 2.75 hours
- P3 Tasks (US6): 2 hours
- Final Validation: 3 hours
- **Total**: ~14 hours (2 working days)

---

## Implementation Strategy

### MVP First (P1 Release Blockers Only)

1. Complete Phase 1: Setup (30 min)
2. Complete Phase 2: Foundational Code Audit (1 hour)
3. Complete Phase 3: US1 - Build Configuration (1 hour)
4. Complete Phase 4: US2 - App Identity (45 min)
5. Complete Phase 5: US3 - Permission Handling (1.5 hours)
6. Complete Phase 6: US7 - Signing Configuration (1.5 hours)
7. **STOP and VALIDATE**: Verify signed APK with core functionality
8. **Decision Point**: Release as RC v1.0 or continue with P2/P3 validation

### Incremental Validation

1. Foundation Ready (Phases 1-2) → Code audit complete
2. Build Validated (Phase 3) → APK generation working
3. Identity Correct (Phase 4) → User-facing branding complete
4. Permissions Safe (Phase 5) → No crash on permission denial
5. Signed & Distributable (Phase 6) → Ready for user installation
6. Hardware Validated (Phase 7) → Printing working on real devices
7. UX Validated (Phase 8) → Rotation handling confirmed
8. Performance Validated (Phase 9) → Memory stability confirmed
9. **Production Ready** (Phase 10) → Full release validation complete

### Testing Strategy

- **P1 tasks**: Focus on build configuration, app identity, permissions, signing (BLOCKERS)
- **P2 tasks**: Hardware validation with actual printers and devices
- **P3 tasks**: Extended testing for performance and memory
- **Edge cases**: Final phase comprehensive testing

### Risk Mitigation

- **Risk**: ProGuard removes Bluetooth classes
  - **Mitigation**: T008 reviews ProGuard rules before build, T019 checks warnings
- **Risk**: Keystore password lost or compromised
  - **Mitigation**: T037 creates keystore.properties (git-ignored), T038 verifies gitignore
- **Risk**: Physical device/printer unavailability
  - **Mitigation**: T003-T004 verify hardware availability before starting
- **Risk**: Bluetooth permission changes between Android versions
  - **Mitigation**: T075-T076 test on minimum and target SDK devices

---

## Notes

- All [US#] labels map tasks to specific user stories for traceability
- P1 tasks (US1, US2, US3, US7) are release blockers and must complete before distribution
- Manual tasks require user intervention and cannot be fully automated
- Keystore and passwords MUST NOT be committed to version control
- Physical devices and thermal printers required for comprehensive validation
- Unit tests must pass before final release approval
- Each phase has clear acceptance criteria and checkpoints for validation
