# Data Model: Quick Amount Receipt Printer

**Feature**: 001-quick-amount-receipt-printer  
**Date**: 2025-12-29  
**Version**: 1.0

## Overview

This document defines all data entities, relationships, validation rules, and state transitions for the Quick Amount Receipt Printer application. Following Clean Architecture, models are divided into domain entities (business logic) and data entities (persistence/transport).

---

## Domain Models

### 1. PrinterDevice

**Purpose**: Represents a Bluetooth printer device

```kotlin
data class PrinterDevice(
    val name: String,
    val macAddress: String
) {
    /**
     * Validates MAC address format (XX:XX:XX:XX:XX:XX)
     */
    fun isValidMacAddress(): Boolean {
        val macRegex = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$".toRegex()
        return macAddress.matches(macRegex)
    }
    
    /**
     * Display name with MAC for UI
     */
    fun displayName(): String = "$name ($macAddress)"
}
```

**Validation Rules**:
- `name`: Non-empty string, max 50 characters
- `macAddress`: Valid Bluetooth MAC format (12 hex digits with colons)

**Usage**: 
- Device selection list
- Last connected printer persistence
- Connection target identification

---

### 2. PrinterStatus (Sealed Class)

**Purpose**: Represents current printer connection state

```kotlin
sealed class PrinterStatus {
    /**
     * No printer connected
     */
    object Disconnected : PrinterStatus()
    
    /**
     * Connection attempt in progress
     */
    object Connecting : PrinterStatus()
    
    /**
     * Successfully connected to printer
     * @param device The connected printer device
     */
    data class Connected(val device: PrinterDevice) : PrinterStatus()
    
    /**
     * UI-friendly status text
     */
    fun toDisplayText(): String = when (this) {
        Disconnected -> "Disconnected"
        Connecting -> "Connecting..."
        is Connected -> "Connected: ${device.name}"
    }
    
    /**
     * Determines if print button should be enabled
     */
    fun canPrint(): Boolean = this is Connected
}
```

**State Transitions**:
```
Disconnected → Connecting (user initiates connection)
Connecting → Connected (connection succeeds)
Connecting → Disconnected (connection fails)
Connected → Disconnected (error or manual disconnect)
Connected → Connecting (reconnection attempt)
```

**Usage**:
- UI status bar display
- Print button enable/disable logic
- Connection flow control

---

### 3. Receipt

**Purpose**: Domain model for receipt data

```kotlin
data class Receipt(
    val businessName: String = "NAEEM DOCUMENTATION",
    val amount: Double,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val currency: String = "SR"
) {
    /**
     * Validates receipt data
     */
    fun validate(): Result<Unit> {
        return when {
            amount <= 0.0 -> Result.failure(IllegalArgumentException("Amount must be positive"))
            amount > 999999.99 -> Result.failure(IllegalArgumentException("Amount too large"))
            businessName.isBlank() -> Result.failure(IllegalArgumentException("Business name required"))
            else -> Result.success(Unit)
        }
    }
    
    /**
     * Format amount for display (2 decimal places)
     */
    fun formattedAmount(): String = String.format("%.2f", amount)
    
    /**
     * Format timestamp for receipt printing
     */
    fun formattedTimestamp(): String = 
        timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}
```

**Validation Rules**:
- `amount`: 0.01 to 999,999.99 (positive, max 6 digits before decimal)
- `businessName`: Non-blank string
- `timestamp`: Valid LocalDateTime
- `currency`: Non-empty string (hardcoded to "SR")

**Business Rules**:
- Amount rounded to 2 decimal places for printing
- Timestamp captured at print time, not input time
- Business name is hardcoded (no customization in v1)

---

### 4. PrintResult (Sealed Class)

**Purpose**: Result of print operation

```kotlin
sealed class PrintResult {
    /**
     * Print completed successfully
     */
    object Success : PrintResult()
    
    /**
     * Print failed with error
     * @param message User-facing error message
     * @param cause Optional exception for logging
     */
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : PrintResult()
    
    /**
     * Convert to Result type for easier handling
     */
    fun toResult(): Result<Unit> = when (this) {
        Success -> Result.success(Unit)
        is Error -> Result.failure(cause ?: Exception(message))
    }
}
```

**Usage**:
- Return type for print operations
- Error message display in UI
- Success feedback (Toast/Snackbar)

---

## Presentation State Models

### 5. MainUiState

**Purpose**: Complete UI state for main screen

```kotlin
data class MainUiState(
    val printerStatus: PrinterStatus = PrinterStatus.Disconnected,
    val amountInput: String = "0.00",
    val isPrinting: Boolean = false,
    val errorMessage: String? = null,
    val availableDevices: List<PrinterDevice> = emptyList(),
    val showDeviceSelection: Boolean = false
) {
    /**
     * Parse amount input to Double
     */
    fun getAmount(): Double? = amountInput.toDoubleOrNull()
    
    /**
     * Check if print button should be enabled
     */
    fun canPrint(): Boolean = 
        printerStatus is PrinterStatus.Connected && 
        !isPrinting && 
        (getAmount() ?: 0.0) > 0.0
    
    /**
     * Check if device selection should show
     */
    fun shouldShowDeviceList(): Boolean = 
        showDeviceSelection && availableDevices.isNotEmpty()
}
```

**State Fields**:
- `printerStatus`: Current connection state
- `amountInput`: Raw input string from number pad (maintains "0.00" format)
- `isPrinting`: Loading state during print operation
- `errorMessage`: Temporary error to display (null when no error)
- `availableDevices`: List of paired Bluetooth printers
- `showDeviceSelection`: Dialog visibility flag

**Validation**:
- Amount must parse to valid Double
- Amount must be positive for printing
- Device list only shown when not empty

---

## Data Layer Models

### 6. PrinterPreference (Internal to data layer)

**Purpose**: SharedPreferences key-value mapping

```kotlin
object PrinterPreferences {
    private const val PREFS_NAME = "printer_prefs"
    private const val KEY_LAST_PRINTER_MAC = "last_printer_mac"
    private const val KEY_LAST_PRINTER_NAME = "last_printer_name"
    
    fun save(context: Context, device: PrinterDevice) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_PRINTER_MAC, device.macAddress)
            .putString(KEY_LAST_PRINTER_NAME, device.name)
            .apply()
    }
    
    fun load(context: Context): PrinterDevice? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val mac = prefs.getString(KEY_LAST_PRINTER_MAC, null) ?: return null
        val name = prefs.getString(KEY_LAST_PRINTER_NAME, "Unknown") ?: "Unknown"
        return PrinterDevice(name, mac)
    }
    
    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
```

**Storage Schema**:
- Key: `last_printer_mac` → Value: String (MAC address)
- Key: `last_printer_name` → Value: String (device name)

**Persistence Rules**:
- Save immediately on successful connection
- Load on app launch for auto-reconnect
- Clear only on explicit user action (future: "Forget printer")

---

## Constants & Enums

### 7. ESC/POS Command Constants

```kotlin
object ESCPOSCommands {
    // Control commands
    val INIT = byteArrayOf(0x1B, 0x40)              // ESC @ - Initialize
    val LF = byteArrayOf(0x0A)                       // Line feed
    
    // Alignment
    val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
    val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
    val ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)
    
    // Text styling
    val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
    val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
    val SIZE_NORMAL = byteArrayOf(0x1D, 0x21, 0x00)
    val SIZE_DOUBLE = byteArrayOf(0x1D, 0x21, 0x11)
    
    // Paper control
    fun feed(lines: Int) = byteArrayOf(0x1B, 0x64, lines.toByte())
    
    // Text encoding
    fun text(str: String) = str.toByteArray(Charsets.UTF_8)
}
```

### 8. Bluetooth Constants

```kotlin
object BluetoothConstants {
    val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    const val CONNECTION_TIMEOUT_MS = 5000L
    const val SOCKET_BUFFER_SIZE = 1024
}
```

---

## Relationships

```
MainUiState
    ├─ printerStatus: PrinterStatus
    │   └─ Connected.device: PrinterDevice
    ├─ availableDevices: List<PrinterDevice>
    └─ errorMessage: String?

Receipt (created from MainUiState.amountInput)
    └─ Used by ReceiptBuilder to generate ESC/POS commands

PrinterDevice
    ├─ Saved to PrinterPreferences
    ├─ Used in BluetoothHelper.connect()
    └─ Displayed in device selection list

PrintResult
    └─ Updates MainUiState.errorMessage or shows success
```

---

## State Flow Diagram

```
[App Launch]
    ↓
[Load saved printer from PrinterPreferences]
    ↓
[Attempt auto-connect] → [Update PrinterStatus]
    ↓
[MainUiState.printerStatus = Connected/Disconnected]
    ↓
[User enters amount] → [Update MainUiState.amountInput]
    ↓
[User taps Print]
    ↓
[Create Receipt from amountInput]
    ↓
[Validate Receipt]
    ↓ (valid)
[Generate ESC/POS commands]
    ↓
[Send via BluetoothHelper]
    ↓
[Return PrintResult] → [Update UI]
```

---

## Data Validation Summary

| Entity | Field | Validation | Error Message |
|--------|-------|------------|---------------|
| PrinterDevice | macAddress | Regex: `^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$` | "Invalid MAC address format" |
| Receipt | amount | 0.01 ≤ amount ≤ 999999.99 | "Amount must be between 0.01 and 999,999.99" |
| Receipt | businessName | !isBlank() | "Business name required" |
| MainUiState | amountInput | toDoubleOrNull() != null | "Invalid amount format" |

---

## Performance Considerations

- **PrinterDevice**: Lightweight data class, safe to copy frequently
- **MainUiState**: Immutable, efficient for StateFlow emission
- **Receipt**: Created only on print action, not on every input
- **ESC/POS commands**: Pre-allocated byte arrays (no runtime allocation)

---

## Testing Strategy

### Unit Tests Required

1. **PrinterDevice.isValidMacAddress()**
   - Valid formats: "AA:BB:CC:DD:EE:FF", "00:11:22:33:44:55"
   - Invalid: "AA:BB:CC", "XX:YY:ZZ:AA:BB:CC", "AABBCCDDEEFF"

2. **Receipt.validate()**
   - Valid: amount = 100.00, 0.01, 999999.99
   - Invalid: amount = 0.00, -10.00, 1000000.00

3. **MainUiState.canPrint()**
   - True: Connected + not printing + amount > 0
   - False: Disconnected / printing / amount ≤ 0

4. **PrinterStatus state transitions**
   - Verify toDisplayText() for all states
   - Verify canPrint() logic

---

## Migration Strategy

**Current Version**: 1.0 (Initial)

**Future Considerations**:
- v1.1: Add optional notes field to Receipt
- v1.2: Multiple business profiles (different PrinterPreferences)
- v2.0: Print history (new Room database entity)

**Backwards Compatibility**: None needed (initial version)

---

**Data Model Status**: ✅ COMPLETE
