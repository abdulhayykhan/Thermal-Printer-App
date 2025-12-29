# Naeem Documentation App Constitution

## Core Principles

### I. Clean Architecture
All features follow Clean Architecture with clear separation of concerns:
- Domain layer: Business logic and entities (independent of framework)
- Data layer: Bluetooth communication and persistence
- Presentation layer: Jetpack Compose UI
- Each layer must be independently testable

### II. Pure Native Implementation
No external printer libraries permitted:
- Direct Bluetooth Classic API usage (android.bluetooth.*)
- Custom ESC/POS command implementation
- No third-party printer SDKs or wrappers
- Full control over printer communication protocol

### III. Coroutines for Concurrency
All asynchronous operations use Kotlin Coroutines:
- Bluetooth operations on Dispatchers.IO
- UI updates on Dispatchers.Main
- Proper error handling with try-catch
- Thread safety guaranteed through structured concurrency

### IV. User-Facing Error Handling
All errors must be communicated to users:
- Toast for transient errors (connection failed, print error)
- Snackbar for recoverable errors with actions
- Clear, actionable error messages (no technical jargon)
- Graceful degradation (continue working if printer disconnected)

### V. Android Best Practices
Follow Android development standards:
- Jetpack Compose for UI (Material Design 3)
- ViewModel for state management
- SharedPreferences for simple persistence
- Runtime permissions with proper user prompts
- Lifecycle-aware components

### VI. Simplicity First
Start with minimal viable implementation:
- Single hardcoded receipt format (no customization)
- Basic amount input only
- Auto-connect to last used printer
- No print history or logs
- YAGNI: Add complexity only when needed

## Technical Constraints

### Platform Requirements
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Bluetooth**: Classic only (no BLE)
- **Permissions**: BLUETOOTH, BLUETOOTH_ADMIN (legacy); BLUETOOTH_CONNECT, BLUETOOTH_SCAN (Android 12+)

### Performance Standards
- **Connection Time**: <3 seconds to paired printer
- **Print Time**: <2 seconds from button press to print start
- **UI Responsiveness**: No ANR (all blocking operations off main thread)
- **Memory**: <50MB RAM usage

### Security & Privacy
- No external network access required
- No data collection or analytics
- Printer MAC address stored locally only
- No sensitive data in logs

## Development Workflow

### Testing Requirements
- Unit tests for business logic (BluetoothHelper, ReceiptBuilder)
- UI tests for critical flows (amount input, print action)
- Manual testing with actual thermal printer required
- Test both connection scenarios (auto-connect success/failure)

### Code Quality Gates
- All Bluetooth operations must be wrapped in try-catch
- No hardcoded strings in UI (use strings.xml)
- Proper Compose state management (no direct mutable state)
- AndroidManifest complete with all required permissions

### Review Checklist
- Constitution compliance verified
- Bluetooth operations on background thread
- Error handling with user feedback
- Permission handling tested on Android 12+
- Manual printer test completed

## Governance

This constitution supersedes all implementation preferences. Any deviation must be:
1. Documented with clear rationale
2. Approved before implementation
3. Added to Complexity Tracking in plan.md

All code reviews must verify:
- Clean Architecture layers respected
- No external printer libraries introduced
- Coroutines used correctly (proper dispatchers)
- User-facing error messages present

**Version**: 1.0.0 | **Ratified**: 2025-12-29 | **Last Amended**: 2025-12-29
