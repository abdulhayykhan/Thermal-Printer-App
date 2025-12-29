# Feature Specification: Quick Amount Receipt Printer

**Feature ID**: 001  
**Status**: Planning  
**Created**: 2025-12-29

## Overview

A simple Android app that allows users to enter an amount and print a receipt to a paired Bluetooth thermal printer with a hardcoded format for "Naeem Documentation" business.

## User Story

**As a** small business owner  
**I want to** quickly print receipts with entered amounts  
**So that** I can provide documentation to customers without manual receipt writing

## Functional Requirements

### FR1: Amount Input
- User can enter a numeric amount using on-screen number pad
- Amount displays in currency format (e.g., 1234.56)
- Maximum 8 digits before decimal, 2 digits after
- Clear/backspace functionality

### FR2: Printer Connection
- App automatically connects to last used Bluetooth printer on launch
- User can select from paired Bluetooth devices if connection fails
- Visual indicator shows connection status (Connected/Disconnected)
- Connection persists across app restarts

### FR3: Receipt Printing
- Single "Print" button triggers receipt generation
- Hardcoded receipt format:
  ```
  ================================
          NAEEM DOCUMENTATION
  ================================
  
  Date: [YYYY-MM-DD HH:MM]
  
  --------------------------------
  Amount:              [XXXX.XX] SR
  --------------------------------
  
  Thank you for your business!
  
  [3 blank lines for cutting]
  ```
- Amount right-aligned with "SR" suffix
- Print succeeds only when printer connected
- Clear amount input after successful print

### FR4: Error Handling
- Toast message on print failure
- Prompt to select printer if none saved
- Reconnection attempt on disconnection
- Clear error messages for user actions

## Non-Functional Requirements

### NFR1: Performance
- Printer connection within 3 seconds
- Print command sent within 2 seconds of button press
- UI remains responsive during Bluetooth operations

### NFR2: Compatibility
- Support Android 8.0 (API 26) and above
- Compatible with ESC/POS thermal printers (58mm or 80mm)
- Bluetooth Classic protocol

### NFR3: Usability
- Single-screen interface
- No configuration required beyond printer selection
- Portrait orientation only
- Material Design 3 styling

### NFR4: Persistence
- Last used printer saved between sessions
- No history or amount logging (privacy)

## Technical Constraints

1. **Pure Native Implementation**: No external printer libraries allowed
2. **Clean Architecture**: Separation of domain, data, and presentation layers
3. **Kotlin Coroutines**: All async operations use structured concurrency
4. **Jetpack Compose**: Modern UI toolkit
5. **Manual ESC/POS**: Custom command implementation required

## Out of Scope

- Receipt customization or templates
- Print history or logs
- Multiple businesses or configurations
- Network printers (WiFi/IP)
- Receipt preview before printing
- Barcode or QR code generation
- Tax calculations

## Success Criteria

1. User can connect to paired printer in <3 taps
2. Receipt prints correctly with formatted amount
3. Auto-reconnection works on app restart
4. No crashes or ANR during normal operation
5. Clear error messages for all failure scenarios

## Dependencies

- Android Bluetooth API (android.bluetooth.*)
- Jetpack Compose (androidx.compose.*)
- Kotlin Coroutines (kotlinx.coroutines.*)
- Material Design 3 components

## Acceptance Tests

### Test 1: First-time Setup
1. Launch app with no saved printer
2. System prompts for printer selection
3. Select paired device
4. Connection indicator shows "Connected"

### Test 2: Happy Path Print
1. Launch app with saved printer (auto-connects)
2. Enter amount "1500.50"
3. Tap "Print" button
4. Receipt prints with correct format and amount
5. Input clears automatically

### Test 3: Disconnection Recovery
1. App connected to printer
2. Turn off printer
3. Status shows "Disconnected"
4. Turn on printer
5. App reconnects automatically

### Test 4: Error Handling
1. Attempt print with no printer connected
2. Toast shows "Printer not connected"
3. Tap status bar
4. Device selection dialog appears

## UI Mockup (Text Description)

```
┌────────────────────────────────┐
│ Naeem Documentation        [≡] │ <- Top Bar
├────────────────────────────────┤
│ Status: ● Connected            │ <- Status Bar
├────────────────────────────────┤
│                                │
│        ┌──────────────┐        │
│        │    0.00 SR   │        │ <- Amount Display
│        └──────────────┘        │
│                                │
│    ┌───┬───┬───┬───────┐      │
│    │ 1 │ 2 │ 3 │  ⌫    │      │
│    ├───┼───┼───┼───────┤      │
│    │ 4 │ 5 │ 6 │  CLR  │      │ <- Number Pad
│    ├───┼───┼───┼───────┤      │
│    │ 7 │ 8 │ 9 │       │      │
│    ├───┴───┼───┤ PRINT │      │
│    │   0   │ . │       │      │
│    └───────┴───┴───────┘      │
│                                │
└────────────────────────────────┘
```

## Revision History

| Version | Date       | Changes               |
|---------|------------|-----------------------|
| 1.0     | 2025-12-29 | Initial specification |
