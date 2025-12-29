# Bluetooth Printer Service Contract

**Version**: 1.0  
**Date**: 2025-12-29  
**Type**: Internal Interface

## Overview

This contract defines the Bluetooth printer communication interface for the data layer. All Bluetooth operations are asynchronous and executed on `Dispatchers.IO` to prevent main thread blocking.

---

## Interface Definition

```kotlin
interface BluetoothPrinterService {
    /**
     * Get list of paired Bluetooth devices
     * 
     * Retrieves devices that have been paired in system Bluetooth settings.
     * Requires BLUETOOTH_CONNECT permission on Android 12+.
     * 
     * @return Flow emitting list of paired devices
     * @throws SecurityException if Bluetooth permissions not granted
     */
    fun getPairedDevices(): Flow<List<PrinterDevice>>
    
    /**
     * Connect to printer by MAC address
     * 
     * Establishes RFCOMM socket connection to specified device using SPP UUID.
     * This is a suspending function that blocks until connection succeeds or fails.
     * Must be called from a coroutine on Dispatchers.IO.
     * 
     * @param macAddress Bluetooth MAC address (format: "XX:XX:XX:XX:XX:XX")
     * @return Result.success(Unit) if connected, Result.failure(Exception) if failed
     * @throws IOException if connection fails
     * @throws SecurityException if permissions missing
     * @throws IllegalArgumentException if MAC address invalid
     */
    suspend fun connect(macAddress: String): Result<Unit>
    
    /**
     * Send byte array to connected printer
     * 
     * Writes ESC/POS command bytes to the output stream of the active socket.
     * Flushes stream after write to ensure data is sent immediately.
     * 
     * @param data ESC/POS command byte array
     * @return Result.success(Unit) if sent, Result.failure(Exception) if failed
     * @throws IOException if not connected or write fails
     * @throws IllegalStateException if called before connection established
     */
    suspend fun send(data: ByteArray): Result<Unit>
    
    /**
     * Disconnect from current printer
     * 
     * Closes the Bluetooth socket and releases resources.
     * Safe to call even if not connected (no-op in that case).
     * After disconnect, must call connect() again before send().
     */
    suspend fun disconnect()
    
    /**
     * Check if printer is currently connected
     * 
     * @return true if socket is connected and open, false otherwise
     */
    fun isConnected(): Boolean
    
    /**
     * Get current connection status
     * 
     * @return PrinterStatus reflecting current state
     */
    fun getStatus(): PrinterStatus
}
```

---

## Implementation Requirements

### Thread Safety
- All suspend functions MUST be called on `Dispatchers.IO`
- Socket operations are blocking and must not run on main thread
- State mutations must be synchronized or use thread-safe collections

### Error Handling
- All exceptions must be caught and wrapped in `Result.failure()`
- User-facing error messages required for all failure cases
- Socket errors should trigger automatic cleanup (close socket)

### Resource Management
- Socket must be closed in `disconnect()`
- InputStream and OutputStream must be closed with socket
- No resource leaks on connection failure

### Timeout Behavior
- `connect()`: 5-second timeout via socket timeout setting
- `send()`: 3-second write timeout (printer buffer full scenario)

---

## Usage Example

```kotlin
class PrinterRepository(
    private val bluetoothService: BluetoothPrinterService,
    private val scope: CoroutineScope
) {
    suspend fun printReceipt(receipt: Receipt): PrintResult {
        return withContext(Dispatchers.IO) {
            if (!bluetoothService.isConnected()) {
                return@withContext PrintResult.Error("Printer not connected")
            }
            
            val commands = ReceiptBuilder.build(receipt)
            
            bluetoothService.send(commands).fold(
                onSuccess = { PrintResult.Success },
                onFailure = { e -> 
                    PrintResult.Error("Print failed: ${e.message}", e)
                }
            )
        }
    }
}
```

---

## Error Scenarios

| Scenario | Method | Exception | Result | User Message |
|----------|--------|-----------|--------|--------------|
| Bluetooth disabled | `getPairedDevices()` | IllegalStateException | Empty list | "Bluetooth is disabled" |
| No permissions | `connect()` | SecurityException | Result.failure | "Bluetooth permission required" |
| Invalid MAC | `connect()` | IllegalArgumentException | Result.failure | "Invalid printer address" |
| Device not found | `connect()` | IOException | Result.failure | "Printer not found" |
| Connection timeout | `connect()` | SocketTimeoutException | Result.failure | "Connection timeout" |
| Connection failed | `connect()` | IOException | Result.failure | "Could not connect to printer" |
| Not connected | `send()` | IllegalStateException | Result.failure | "Printer not connected" |
| Socket closed | `send()` | IOException | Result.failure | "Printer disconnected" |
| Write timeout | `send()` | SocketTimeoutException | Result.failure | "Printer not responding" |

---

## State Diagram

```
[DISCONNECTED]
    │
    ├─ connect() ───→ [CONNECTING]
    │                      │
    │                      ├─ success ───→ [CONNECTED]
    │                      │                    │
    │                      │                    ├─ send() ───→ [CONNECTED]
    │                      │                    │
    │                      │                    ├─ send() fails ───→ [DISCONNECTED]
    │                      │                    │
    │                      │                    └─ disconnect() ───→ [DISCONNECTED]
    │                      │
    │                      └─ failure ───→ [DISCONNECTED]
    │
    └─ disconnect() ───→ [DISCONNECTED] (no-op)
```

---

## Testing Requirements

### Unit Tests
- Mock BluetoothAdapter and BluetoothSocket
- Verify state transitions
- Test timeout scenarios with delay simulation
- Verify exception handling and Result wrapping

### Integration Tests
- Actual connection to paired test printer
- Send simple ESC/POS commands (init, text, feed)
- Verify reconnection after disconnect
- Test permission denial scenario on Android 12+

---

## Dependencies

- `android.bluetooth.BluetoothAdapter`
- `android.bluetooth.BluetoothDevice`
- `android.bluetooth.BluetoothSocket`
- `kotlinx.coroutines.flow.Flow`
- `kotlinx.coroutines.Dispatchers`
- Domain models: `PrinterDevice`, `PrinterStatus`

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2025-12-29 | Initial contract definition |

---

**Contract Status**: ✅ APPROVED
