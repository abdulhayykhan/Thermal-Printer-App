# Implementation Plan: Quick Amount Receipt Printer

**Branch**: `001-quick-amount-receipt-printer` | **Date**: 2025-12-29 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/001-quick-amount-receipt-printer/spec.md`

## Summary

Android app for quick receipt printing with Bluetooth thermal printers. User enters an amount, taps print, and receives a formatted receipt for "Naeem Documentation" business. Implementation uses pure native Android Bluetooth APIs with custom ESC/POS command generation, following Clean Architecture with Jetpack Compose UI and Kotlin Coroutines for concurrency.

## Technical Context

**Language/Version**: Kotlin 1.9+ with Android SDK  
**Primary Dependencies**: Jetpack Compose (UI), Kotlin Coroutines, Material Design 3, AndroidX Core  
**Storage**: SharedPreferences (last printer MAC address only)  
**Testing**: JUnit 4, Compose UI Testing, Mockito/MockK for unit tests  
**Target Platform**: Android 8.0+ (API 26), Target SDK 34  
**Project Type**: Mobile (Android single-module application)  
**Performance Goals**: <3s connection, <2s print initiation, <50MB RAM  
**Constraints**: Bluetooth Classic only, offline-only (no network), manual ESC/POS commands  
**Scale/Scope**: Single screen, ~5 core classes, 1 device connection, no background service

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Initial Check (Before Phase 0)

| Principle | Requirement | Status | Notes |
|-----------|-------------|--------|-------|
| **I. Clean Architecture** | Domain/Data/Presentation separation | ✅ PASS | Will implement: domain (printer commands), data (Bluetooth manager), presentation (Compose UI) |
| **II. Pure Native** | No external printer libraries | ✅ PASS | Using android.bluetooth.* directly, custom ESC/POS builder |
| **III. Coroutines** | All async on proper dispatchers | ✅ PASS | Bluetooth ops on IO, UI on Main, structured concurrency |
| **IV. Error Handling** | User-facing messages for all errors | ✅ PASS | Toast/Snackbar planned for connection/print failures |
| **V. Android Best Practices** | Compose, ViewModel, runtime permissions | ✅ PASS | Following modern Android architecture |
| **VI. Simplicity** | Minimal viable implementation | ✅ PASS | Single hardcoded format, no history, basic features only |

**Performance Standards:**
- Connection: <3s target ✅ (Bluetooth Classic typical)
- Print: <2s target ✅ (ESC/POS commands are fast)
- Memory: <50MB target ✅ (minimal app, no caching)

**Security/Privacy:**
- No network access ✅
- Local storage only ✅
- No sensitive data logging ✅

**Overall Gate Status**: ✅ **PASS** - All constitutional requirements met, proceeding to Phase 0.

---

### Post-Phase 1 Re-check

| Principle | Design Compliance | Status | Evidence |
|-----------|-------------------|--------|----------|
| **I. Clean Architecture** | Layers clearly defined | ✅ PASS | domain/ (Receipt, ReceiptFormatter), data/ (BluetoothHelper, PrinterPreferences), presentation/ (ViewModel, Compose UI) |
| **II. Pure Native** | No external dependencies | ✅ PASS | Only android.bluetooth.*, kotlinx.coroutines, androidx.compose - all first-party |
| **III. Coroutines** | Proper dispatcher usage | ✅ PASS | BluetoothHelper.connect/send marked suspend, documented for Dispatchers.IO |
| **IV. Error Handling** | User messages defined | ✅ PASS | PrinterError sealed class with toUserMessage(), 8 error scenarios covered |
| **V. Android Best Practices** | Modern Android stack | ✅ PASS | Jetpack Compose, StateFlow, ViewModel, Material 3, runtime permissions with ActivityResultContract |
| **VI. Simplicity** | Minimal scope maintained | ✅ PASS | 10 ESC/POS commands, 5 domain models, 1 screen, hardcoded format only |

**Design Artifacts Validated:**
- ✅ data-model.md: 8 entities, clear validation rules
- ✅ contracts/bluetooth-service.md: Thread-safe interface, proper error handling
- ✅ contracts/receipt-formatter.md: Pure functions, no I/O blocking
- ✅ research.md: All technical decisions justified and alternatives documented

**Performance Re-validation:**
- Connection: Research confirms 1.5-2.5s typical ✅
- Print: <100ms command transmission + printer speed ✅
- Memory: ~300 bytes per receipt, no caching ✅

**Security Re-validation:**
- Manifest permissions correctly scoped (maxSdkVersion, neverForLocation) ✅
- SharedPreferences only stores MAC address (no sensitive data) ✅
- No network permissions declared ✅

**Overall Re-check Status**: ✅ **PASS** - Design fully compliant with constitution. Cleared to proceed to Phase 2 (Task Breakdown).

---

**Constitutional Compliance**: ✅ **VERIFIED** at both gates

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/sp.plan command output)
├── research.md          # Phase 0 output (/sp.plan command)
├── data-model.md        # Phase 1 output (/sp.plan command)
├── quickstart.md        # Phase 1 output (/sp.plan command)
├── contracts/           # Phase 1 output (/sp.plan command)
└── tasks.md             # Phase 2 output (/sp.tasks command - NOT created by /sp.plan)
```

### Source Code (repository root)

```text
app/
├── src/
│   ├── main/
│   │   ├── java/com/naeem/thermalprinter/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── PrinterDevice.kt        # Data class: name, macAddress
│   │   │   │   │   └── PrinterStatus.kt        # Sealed class: Disconnected, Connecting, Connected
│   │   │   │   └── printer/
│   │   │   │       └── ReceiptBuilder.kt       # ESC/POS command generator
│   │   │   │
│   │   │   ├── data/
│   │   │   │   ├── bluetooth/
│   │   │   │   │   └── BluetoothHelper.kt      # Bluetooth operations (scan, connect, send)
│   │   │   │   └── repository/
│   │   │   │       └── PrinterPreferences.kt   # SharedPreferences wrapper
│   │   │   │
│   │   │   └── presentation/
│   │   │       ├── MainActivity.kt              # Entry point, permission handling
│   │   │       ├── MainViewModel.kt             # State management, business logic
│   │   │       ├── ui/
│   │   │       │   ├── MainScreen.kt            # Main Compose screen
│   │   │       │   ├── components/
│   │   │       │   │   ├── StatusBar.kt         # Connection status display
│   │   │       │   │   ├── AmountDisplay.kt     # Amount input display
│   │   │       │   │   ├── NumberPad.kt         # Number input pad
│   │   │       │   │   └── PrintButton.kt       # Print action button
│   │   │       │   └── theme/
│   │   │       │       ├── Color.kt
│   │   │       │       ├── Theme.kt
│   │   │       │       └── Type.kt
│   │   │       └── navigation/
│   │   │           └── DeviceSelectionDialog.kt # Bluetooth device picker
│   │   │
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── drawable/
│   │   │       └── ic_bluetooth.xml
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   └── test/
│       └── java/com/naeem/thermalprinter/
│           ├── domain/
│           │   └── ReceiptBuilderTest.kt        # Unit test ESC/POS commands
│           ├── data/
│           │   └── BluetoothHelperTest.kt       # Mock Bluetooth operations
│           └── presentation/
│               └── MainViewModelTest.kt         # ViewModel logic tests
│
├── build.gradle.kts                             # Module-level Gradle
└── proguard-rules.pro

build.gradle.kts                                 # Project-level Gradle
settings.gradle.kts
gradle.properties
```

**Structure Decision**: Standard Android single-module application using Clean Architecture layers. The `domain` layer contains business logic (ESC/POS commands, models), `data` layer handles Bluetooth I/O and persistence, and `presentation` layer implements Jetpack Compose UI following MVVM pattern. This structure supports independent testing of each layer and clear separation of concerns per constitutional requirement I.

## Complexity Tracking

> No constitutional violations detected. This section intentionally left empty.

---

## Implementation Phases

### Phase 0: Research & Technical Decisions

**Status**: Not Started  
**Output**: `research.md`  
**Duration**: 1-2 hours

#### Research Tasks

1. **ESC/POS Command Set**
   - Research: Standard ESC/POS commands for text formatting
   - Decision needed: Command bytes for init, align, bold, size, feed, cut
   - Output: Command reference table in research.md

2. **Bluetooth Classic Connection Pattern**
   - Research: Android Bluetooth Classic connection lifecycle
   - Decision needed: UUID for SPP (Serial Port Profile)
   - Output: Connection flow diagram

3. **Permission Handling Strategy**
   - Research: Android 12+ Bluetooth permission changes
   - Decision needed: Runtime permission request approach
   - Output: Permission compatibility matrix (API 26-34)

4. **State Management Pattern**
   - Research: Jetpack Compose state hoisting best practices
   - Decision needed: ViewModel structure for printer state
   - Output: State flow architecture

5. **Error Scenarios**
   - Research: Common Bluetooth thermal printer failure modes
   - Decision needed: User-facing error message mapping
   - Output: Error handling decision tree

#### Research Questions to Resolve

- What is the standard UUID for Bluetooth SPP printers? (Typically: 00001101-0000-1000-8000-00805F9B34FB)
- Which ESC/POS commands are universally supported? (ESC @, ESC a, ESC E, GS !, LF, ESC d)
- How to detect printer buffer overflow? (Monitor OutputStream exceptions)
- What's the recommended socket timeout? (3-5 seconds for connection)
- Should we use BluetoothSocket.connect() or ConnectThread? (ConnectThread for non-blocking)

---

### Phase 1: Design & Contracts

**Status**: Not Started  
**Prerequisites**: research.md complete  
**Output**: `data-model.md`, `contracts/`, `quickstart.md`  
**Duration**: 2-3 hours

#### 1.1 Data Model Design

**Output**: `data-model.md`

##### Entities

1. **PrinterDevice**
   ```kotlin
   data class PrinterDevice(
       val name: String,
       val macAddress: String
   )
   ```

2. **PrinterStatus** (Sealed Class)
   ```kotlin
   sealed class PrinterStatus {
       object Disconnected : PrinterStatus()
       object Connecting : PrinterStatus()
       data class Connected(val device: PrinterDevice) : PrinterStatus()
   }
   ```

3. **Receipt** (Domain Model)
   ```kotlin
   data class Receipt(
       val businessName: String = "NAEEM DOCUMENTATION",
       val amount: Double,
       val timestamp: LocalDateTime,
       val currency: String = "SR"
   )
   ```

4. **PrintResult** (Sealed Class)
   ```kotlin
   sealed class PrintResult {
       object Success : PrintResult()
       data class Error(val message: String) : PrintResult()
   }
   ```

##### State Management

**MainViewModel State**
```kotlin
data class MainUiState(
    val printerStatus: PrinterStatus = PrinterStatus.Disconnected,
    val amountInput: String = "0.00",
    val isPrinting: Boolean = false,
    val errorMessage: String? = null,
    val availableDevices: List<PrinterDevice> = emptyList(),
    val showDeviceSelection: Boolean = false
)
```

#### 1.2 API Contracts

**Output**: `contracts/bluetooth-interface.md`, `contracts/printer-commands.md`

##### Bluetooth Interface Contract

```kotlin
interface BluetoothPrinterService {
    /**
     * Get list of paired Bluetooth devices
     * @return Flow of paired devices
     * @throws SecurityException if Bluetooth permissions not granted
     */
    fun getPairedDevices(): Flow<List<PrinterDevice>>
    
    /**
     * Connect to printer by MAC address
     * @param macAddress Bluetooth MAC address
     * @return Result with connection status
     * @throws IOException if connection fails
     */
    suspend fun connect(macAddress: String): Result<Unit>
    
    /**
     * Send byte array to connected printer
     * @param data ESC/POS command bytes
     * @return Result with success/failure
     * @throws IOException if not connected or write fails
     */
    suspend fun send(data: ByteArray): Result<Unit>
    
    /**
     * Disconnect from current printer
     */
    suspend fun disconnect()
    
    /**
     * Check if printer is currently connected
     */
    fun isConnected(): Boolean
}
```

##### ESC/POS Command Contract

```kotlin
interface ReceiptFormatter {
    /**
     * Generate complete receipt bytes
     * @param receipt Receipt data model
     * @return ESC/POS command byte array
     */
    fun format(receipt: Receipt): ByteArray
    
    /**
     * Initialize printer (reset to defaults)
     */
    fun initialize(): ByteArray
    
    /**
     * Set text alignment
     * @param alignment CENTER, LEFT, RIGHT
     */
    fun setAlignment(alignment: Alignment): ByteArray
    
    /**
     * Set text bold
     * @param enabled true for bold, false for normal
     */
    fun setBold(enabled: Boolean): ByteArray
    
    /**
     * Set text size
     * @param size NORMAL, LARGE, XLARGE
     */
    fun setTextSize(size: TextSize): ByteArray
    
    /**
     * Add text line
     * @param text String to print
     */
    fun addText(text: String): ByteArray
    
    /**
     * Feed paper
     * @param lines Number of lines to feed
     */
    fun feed(lines: Int): ByteArray
}
```

#### 1.3 Quickstart Guide

**Output**: `quickstart.md`

Will include:
- Prerequisites (Android Studio, physical device with Bluetooth printer)
- Project setup steps
- Running the app
- Testing checklist
- Common issues and solutions

---

### Phase 2: Task Breakdown

**Status**: Not Started  
**Prerequisites**: Phase 1 complete  
**Output**: `tasks.md` (generated by `/sp.tasks` command, NOT by this plan)  
**Duration**: 30 minutes

Phase 2 will decompose implementation into atomic tasks:
1. Foundation setup (Gradle, Manifest, strings)
2. Domain layer implementation (models, ReceiptBuilder)
3. Data layer implementation (BluetoothHelper, preferences)
4. Presentation layer (ViewModel, Compose UI)
5. Integration and testing

Tasks will be created following the 5-phase strategy provided in the prompt:
- Phase 1: Foundation & Manifest
- Phase 2: Bluetooth Manager
- Phase 3: ESC/POS Command Builder
- Phase 4: UI Implementation
- Phase 5: Wiring & Permissions

---

## Development Workflow

### Testing Strategy

1. **Unit Tests** (domain/data layers)
   - ReceiptBuilder output validation
   - BluetoothHelper mock tests
   - ViewModel state transitions

2. **UI Tests** (presentation layer)
   - Amount input behavior
   - Print button enable/disable logic
   - Device selection dialog

3. **Manual Integration Tests** (with actual printer)
   - Connection to real device
   - Receipt print quality
   - Error handling scenarios
   - Permission flows on different Android versions

### Build Configuration

**build.gradle (Module)**
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.naeem.thermalprinter"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.naeem.thermalprinter"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

**AndroidManifest.xml Requirements**
```xml
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" />

<application
    android:allowBackup="false"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/Theme.ThermalPrinter">
    
    <activity
        android:name=".presentation.MainActivity"
        android:exported="true"
        android:screenOrientation="portrait">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

### Quality Gates

Before merging:
1. ✅ All unit tests pass
2. ✅ UI tests pass on emulator
3. ✅ Manual test with real printer successful
4. ✅ Permission flows tested on Android 12+ and pre-12
5. ✅ No hardcoded strings in UI (all in strings.xml)
6. ✅ All Bluetooth operations on Dispatchers.IO
7. ✅ Error handling provides user feedback
8. ✅ Constitution compliance re-verified

---

## Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Printer compatibility issues | High | Research common ESC/POS subset, test with multiple printer models |
| Android 12+ permission complexity | Medium | Implement proper runtime permission flow, test on multiple API levels |
| Bluetooth connection instability | Medium | Implement reconnection logic, proper error handling, socket timeouts |
| ESC/POS command errors | Medium | Test command sequences incrementally, validate with simple prints first |
| Threading issues with Bluetooth | Low | Strict use of Dispatchers.IO, proper coroutine scoping |

---

## Success Metrics

- [ ] App connects to paired printer in <3 taps
- [ ] Receipt prints with correct format and alignment
- [ ] Auto-reconnection works on app restart
- [ ] Clear error messages for all failure scenarios
- [ ] No crashes during normal operation
- [ ] All constitutional principles validated

---

## Next Steps

1. **Review this plan** for completeness and constitutional compliance
2. **Begin Phase 0**: Execute research tasks, generate `research.md`
3. **Complete Phase 1**: Generate data-model.md, contracts/, quickstart.md
4. **Run agent context update**: `.specify/scripts/bash/update-agent-context.sh copilot`
5. **Re-verify Constitution Check** after design phase
6. **Generate tasks.md** via `/sp.tasks` command (Phase 2)

---

**Plan Status**: ✅ COMPLETE - Ready for Phase 0 research
