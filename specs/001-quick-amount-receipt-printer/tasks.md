---

description: "Task list for Quick Amount Receipt Printer feature implementation"
---

# Tasks: Quick Amount Receipt Printer

**Input**: Design documents from `/specs/001-quick-amount-receipt-printer/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Organization**: Tasks are grouped by implementation phase following Clean Architecture layers and dependency ordering.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[US1]**: Maps to User Story 1 (single story for this MVP feature)
- Include exact file paths in descriptions

## Path Conventions

- **Android project**: `app/src/main/java/com/naeem/thermalprinter/`
- **Resources**: `app/src/main/res/`
- **Tests**: `app/src/test/java/com/naeem/thermalprinter/`
- **Manifest**: `app/src/main/AndroidManifest.xml`

---

## Phase 1: Setup (Project Foundation)

**Purpose**: Project initialization, Gradle configuration, and basic structure

- [ ] T001 Create project directory structure following Clean Architecture (domain/, data/, presentation/)
- [ ] T002 Initialize Gradle build files with Kotlin 1.9+, SDK 34, minSdk 26 in build.gradle.kts
- [ ] T003 [P] Add Jetpack Compose dependencies to app/build.gradle.kts
- [ ] T004 [P] Add Kotlin Coroutines dependencies to app/build.gradle.kts
- [ ] T005 [P] Configure AndroidManifest.xml with Bluetooth permissions (BLUETOOTH, BLUETOOTH_ADMIN, BLUETOOTH_CONNECT, BLUETOOTH_SCAN)
- [ ] T006 [P] Create string resources in app/src/main/res/values/strings.xml (app name, error messages, UI labels)
- [ ] T007 [P] Create Material 3 theme resources in app/src/main/res/values/colors.xml and themes.xml
- [ ] T008 [P] Add Bluetooth icon drawable to app/src/main/res/drawable/ic_bluetooth.xml

**Checkpoint**: Project structure ready, dependencies configured, manifest declared

---

## Phase 2: Foundational (Core Infrastructure)

**Purpose**: Domain models and constants that all layers depend on

**⚠️ CRITICAL**: No implementation work can begin until this phase is complete

- [ ] T009 [P] Create PrinterDevice data class in app/src/main/java/com/naeem/thermalprinter/domain/model/PrinterDevice.kt
- [ ] T010 [P] Create PrinterStatus sealed class in app/src/main/java/com/naeem/thermalprinter/domain/model/PrinterStatus.kt
- [ ] T011 [P] Create Receipt data class with validation in app/src/main/java/com/naeem/thermalprinter/domain/model/Receipt.kt
- [ ] T012 [P] Create PrintResult sealed class in app/src/main/java/com/naeem/thermalprinter/domain/model/PrintResult.kt
- [ ] T013 [P] Create ESCPOSCommands constants object in app/src/main/java/com/naeem/thermalprinter/domain/printer/ESCPOSCommands.kt
- [ ] T014 [P] Create Alignment enum in app/src/main/java/com/naeem/thermalprinter/domain/printer/Alignment.kt
- [ ] T015 [P] Create TextSize enum in app/src/main/java/com/naeem/thermalprinter/domain/printer/TextSize.kt
- [ ] T016 [P] Create BluetoothConstants object in app/src/main/java/com/naeem/thermalprinter/data/bluetooth/BluetoothConstants.kt

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Quick Amount Receipt Printing (Priority: P1) 🎯 MVP

**Goal**: User can enter an amount, connect to a Bluetooth thermal printer, and print a formatted receipt for "Naeem Documentation" business with hardcoded layout.

**Independent Test**: 
1. Launch app → Grant Bluetooth permissions
2. Select paired printer from device list → Status shows "Connected"
3. Enter amount "1500.50" using number pad
4. Tap PRINT button → Receipt prints with correct format, amount right-aligned with "SR", date timestamp, business name centered
5. Amount input clears automatically after successful print

### Implementation for User Story 1

#### Data Layer (Bluetooth & Persistence)

- [ ] T017 [P] [US1] Implement PrinterPreferences object for SharedPreferences in app/src/main/java/com/naeem/thermalprinter/data/repository/PrinterPreferences.kt
- [ ] T018 [US1] Implement BluetoothHelper class with SPP UUID connection in app/src/main/java/com/naeem/thermalprinter/data/bluetooth/BluetoothHelper.kt
- [ ] T019 [US1] Add getPairedDevices() method to BluetoothHelper (requires BLUETOOTH_CONNECT permission)
- [ ] T020 [US1] Add connect(macAddress: String) suspend function to BluetoothHelper on Dispatchers.IO
- [ ] T021 [US1] Add send(data: ByteArray) suspend function to BluetoothHelper with flush() and error handling
- [ ] T022 [US1] Add disconnect() and isConnected() methods to BluetoothHelper with socket cleanup
- [ ] T023 [US1] Add getStatus() method to BluetoothHelper returning PrinterStatus

#### Domain Layer (ESC/POS Formatting)

- [ ] T024 [US1] Implement ReceiptBuilder object with format(receipt: Receipt) function in app/src/main/java/com/naeem/thermalprinter/domain/printer/ReceiptBuilder.kt
- [ ] T025 [US1] Add initialize() function returning ESC @ command bytes to ReceiptBuilder
- [ ] T026 [US1] Add setAlignment(alignment: Alignment) function to ReceiptBuilder
- [ ] T027 [US1] Add setBold(enabled: Boolean) function to ReceiptBuilder
- [ ] T028 [US1] Add setTextSize(size: TextSize) function to ReceiptBuilder
- [ ] T029 [US1] Add addText(text: String) function with UTF-8 encoding and LF to ReceiptBuilder
- [ ] T030 [US1] Add feed(lines: Int) function with validation (1-255) to ReceiptBuilder
- [ ] T031 [US1] Implement complete receipt format sequence in ReceiptBuilder.format() per spec Section 4

#### Presentation Layer (ViewModel State Management)

- [ ] T032 [US1] Create MainUiState data class in app/src/main/java/com/naeem/thermalprinter/presentation/MainViewModel.kt
- [ ] T033 [US1] Create MainViewModel class with StateFlow<MainUiState> in app/src/main/java/com/naeem/thermalprinter/presentation/MainViewModel.kt
- [ ] T034 [US1] Inject BluetoothHelper and PrinterPreferences into MainViewModel constructor
- [ ] T035 [US1] Add initializeBluetooth() function to MainViewModel (load saved printer, attempt auto-connect)
- [ ] T036 [US1] Add onAmountInput(digit: String) function to MainViewModel for number pad handling
- [ ] T037 [US1] Add onClearAmount() function to MainViewModel
- [ ] T038 [US1] Add onBackspace() function to MainViewModel for amount editing
- [ ] T039 [US1] Add onDeviceSelectionRequested() function to MainViewModel (show device list dialog)
- [ ] T040 [US1] Add onDeviceSelected(device: PrinterDevice) function to MainViewModel (connect + save to preferences)
- [ ] T041 [US1] Add onPrintClicked() coroutine function to MainViewModel (create Receipt, format, send, update state)
- [ ] T042 [US1] Add error handling with user-facing messages in MainViewModel using PrinterError.toUserMessage()

#### UI Layer (Jetpack Compose)

- [ ] T043 [P] [US1] Create StatusBar composable in app/src/main/java/com/naeem/thermalprinter/presentation/ui/components/StatusBar.kt
- [ ] T044 [P] [US1] Create AmountDisplay composable in app/src/main/java/com/naeem/thermalprinter/presentation/ui/components/AmountDisplay.kt
- [ ] T045 [P] [US1] Create NumberPad composable in app/src/main/java/com/naeem/thermalprinter/presentation/ui/components/NumberPad.kt
- [ ] T046 [P] [US1] Create PrintButton composable in app/src/main/java/com/naeem/thermalprinter/presentation/ui/components/PrintButton.kt
- [ ] T047 [US1] Create DeviceSelectionDialog composable in app/src/main/java/com/naeem/thermalprinter/presentation/ui/navigation/DeviceSelectionDialog.kt
- [ ] T048 [US1] Create MainScreen composable in app/src/main/java/com/naeem/thermalprinter/presentation/ui/MainScreen.kt (integrate all components)
- [ ] T049 [US1] Add Material 3 theme wrapper in app/src/main/java/com/naeem/thermalprinter/presentation/ui/theme/Theme.kt
- [ ] T050 [P] [US1] Define Color scheme in app/src/main/java/com/naeem/thermalprinter/presentation/ui/theme/Color.kt
- [ ] T051 [P] [US1] Define Typography in app/src/main/java/com/naeem/thermalprinter/presentation/ui/theme/Type.kt

#### Integration & Permissions

- [ ] T052 [US1] Implement MainActivity in app/src/main/java/com/naeem/thermalprinter/presentation/MainActivity.kt
- [ ] T053 [US1] Add ActivityResultContract for Bluetooth permissions in MainActivity (BLUETOOTH_CONNECT, BLUETOOTH_SCAN on API 31+)
- [ ] T054 [US1] Add onCreate() setup in MainActivity (request permissions, initialize ViewModel, set Compose content)
- [ ] T055 [US1] Add permission handling callbacks in MainActivity with rationale dialog
- [ ] T056 [US1] Wire MainScreen to MainViewModel in MainActivity.onCreate()
- [ ] T057 [US1] Configure portrait-only orientation in AndroidManifest.xml for MainActivity
- [ ] T058 [US1] Add launcher intent filter to MainActivity in AndroidManifest.xml

**Checkpoint**: User Story 1 is fully functional and testable independently - MVP complete!

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Improvements, documentation, and validation

- [ ] T059 [P] Add Toast/Snackbar display for error messages in MainScreen
- [ ] T060 [P] Add loading indicator during connection/printing operations in UI
- [ ] T061 [P] Implement auto-reconnection logic on print failure in MainViewModel
- [ ] T062 [P] Add string resource validation (no hardcoded UI strings)
- [ ] T063 [P] Code cleanup and KDoc comments for public APIs
- [ ] T064 Run quickstart.md validation checklist with real printer
- [ ] T065 Manual testing: first-time setup flow on Android 12+ device
- [ ] T066 Manual testing: auto-reconnect on app restart
- [ ] T067 Manual testing: error handling (printer off, disconnection, invalid amount)
- [ ] T068 Manual testing: receipt format validation (alignment, spacing, font sizes)
- [ ] T069 Constitution compliance check (Clean Architecture, thread safety, error messages)
- [ ] T070 Performance validation (connection <3s, print <2s, no ANR)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user story work
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
  - Data layer can start immediately after Phase 2
  - Domain layer can start in parallel with Data layer
  - Presentation layer (ViewModel) depends on Data + Domain layers
  - UI layer depends on ViewModel
  - Integration depends on all layers
- **Polish (Phase 4)**: Depends on User Story 1 completion

### Within User Story 1 (Dependency Order)

1. **Data Layer** (T017-T023): Can start after Foundational complete
2. **Domain Layer** (T024-T031): Can start in parallel with Data layer
3. **ViewModel** (T032-T042): Depends on Data + Domain layers complete
4. **UI Components** (T043-T051): Depends on ViewModel state structure defined (T032)
5. **Integration** (T052-T058): Depends on ViewModel + UI complete

### Parallel Opportunities

- **Phase 1 Setup**: Tasks T003-T008 (all marked [P]) can run in parallel
- **Phase 2 Foundational**: Tasks T009-T016 (all marked [P]) can run in parallel
- **Within Data Layer**: T017 can run parallel with T018-T023 (different concerns)
- **Domain Layer**: All tasks T024-T031 sequential (build ReceiptBuilder incrementally)
- **ViewModel Functions**: Tasks T035-T042 sequential (depend on core setup)
- **UI Components**: Tasks T043-T046, T050-T051 (all marked [P]) can run in parallel
- **Polish**: Tasks T059-T063 (all marked [P]) can run in parallel after US1 complete

---

## Parallel Example: User Story 1

### Launch Phase 2 (Foundation) Together:
```bash
Task T009: "Create PrinterDevice data class"
Task T010: "Create PrinterStatus sealed class"
Task T011: "Create Receipt data class"
Task T012: "Create PrintResult sealed class"
Task T013: "Create ESCPOSCommands constants"
Task T014: "Create Alignment enum"
Task T015: "Create TextSize enum"
Task T016: "Create BluetoothConstants object"
```

### Launch UI Components Together:
```bash
Task T043: "Create StatusBar composable"
Task T044: "Create AmountDisplay composable"
Task T045: "Create NumberPad composable"
Task T046: "Create PrintButton composable"
Task T050: "Define Color scheme"
Task T051: "Define Typography"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T008) → Project structure ready
2. Complete Phase 2: Foundational (T009-T016) → CRITICAL blocking phase
3. Complete Phase 3: User Story 1 (T017-T058) → Full feature implementation
   - Data Layer → Domain Layer (parallel)
   - Then ViewModel → UI Components (sequential)
   - Then Integration
4. **STOP and VALIDATE**: Test User Story 1 independently with real printer
5. Complete Phase 4: Polish (T059-T070) → Production ready
6. Deploy/demo

### Incremental Testing Points

1. **After T016 (Foundation)**: Verify all models compile, run unit tests on validation logic
2. **After T023 (Data Layer)**: Mock Bluetooth operations, verify connection flow
3. **After T031 (Domain Layer)**: Unit test ESC/POS command generation, verify byte sequences
4. **After T042 (ViewModel)**: Unit test state transitions, verify business logic
5. **After T051 (UI Components)**: Compose preview tests, verify component rendering
6. **After T058 (Integration)**: End-to-end manual test with real printer
7. **After T070 (Polish)**: Full quickstart.md validation, performance benchmarks

---

## Thread Safety Notes

**CRITICAL Threading Requirements**:

- **T020 (connect)**: Must execute on Dispatchers.IO (blocking socket operation)
- **T021 (send)**: Must execute on Dispatchers.IO (blocking write operation)
- **T041 (onPrintClicked)**: Launch coroutine with viewModelScope + Dispatchers.IO for Bluetooth ops
- **T042 (error handling)**: Update UI state on Dispatchers.Main via StateFlow
- All Bluetooth socket operations MUST NOT run on main thread (ANR risk)

---

## Testing Strategy (Manual - No Automated Tests Required)

This feature does not require automated tests. Validation is through manual testing with real hardware.

### Manual Test Scenarios (Phase 4)

1. **First-Time Setup (T065)**:
   - Fresh install on Android 12+ device
   - Verify permission request dialog
   - Grant permissions → verify device selection prompt

2. **Auto-Reconnect (T066)**:
   - Connect to printer
   - Close app completely
   - Relaunch → verify auto-connection within 3 seconds

3. **Error Handling (T067)**:
   - Attempt print with printer off → verify "Printer not connected" toast
   - Turn off printer during connection → verify "Printer disconnected" message
   - Enter 0.00 → verify Print button disabled

4. **Receipt Format (T068)**:
   - Print receipt with amount 1500.50
   - Verify business name centered and bold
   - Verify amount right-aligned with "SR"
   - Verify date format correct
   - Verify 3 blank lines for cutting

5. **Quickstart Validation (T064)**:
   - Follow quickstart.md step by step
   - Verify all test cases pass
   - Document any deviations or issues

---

## Notes

- [P] tasks = different files, no dependencies within phase
- [US1] label maps all tasks to single user story for traceability
- User Story 1 is the complete MVP feature
- Verify no main thread blocking in Bluetooth operations (T020, T021, T041)
- All error scenarios must show user-facing messages (T042, T059)
- Commit after each logical group or completed component
- Stop at any checkpoint to validate progress independently
- Constitution compliance: Clean Architecture respected, pure native implementation, proper coroutine usage

---

## Summary

- **Total Tasks**: 70
- **Setup Phase**: 8 tasks
- **Foundational Phase**: 8 tasks (BLOCKS all subsequent work)
- **User Story 1 Phase**: 42 tasks
  - Data Layer: 7 tasks
  - Domain Layer: 8 tasks
  - Presentation Layer: 11 tasks
  - UI Layer: 9 tasks
  - Integration: 7 tasks
- **Polish Phase**: 12 tasks
- **Parallel Opportunities**: 20 tasks marked [P]
- **Independent Test Criteria**: Manual testing with real printer (no automated tests)
- **MVP Scope**: Complete Phase 1-3 (User Story 1)
- **Production Ready**: Complete all 4 phases including polish

**Format Validation**: ✅ All tasks follow checklist format (checkbox, ID, optional [P], [US1] for story tasks, file paths)

---

**Tasks Status**: ✅ COMPLETE - Ready for execution
