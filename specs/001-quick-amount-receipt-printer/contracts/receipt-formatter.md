# Receipt Formatter Contract

**Version**: 1.0  
**Date**: 2025-12-29  
**Type**: Internal Interface

## Overview

This contract defines the ESC/POS command generation interface for receipt formatting. The formatter translates domain Receipt objects into byte arrays that can be sent to thermal printers.

---

## Interface Definition

```kotlin
interface ReceiptFormatter {
    /**
     * Generate complete receipt bytes
     * 
     * Takes a Receipt domain object and produces a complete ESC/POS command sequence
     * for printing. The output includes initialization, formatting, content, and 
     * paper feed commands.
     * 
     * @param receipt Receipt data model with amount, timestamp, business name
     * @return ESC/POS command byte array ready to send to printer
     * @throws IllegalArgumentException if receipt validation fails
     */
    fun format(receipt: Receipt): ByteArray
    
    /**
     * Initialize printer
     * 
     * Sends ESC @ command to reset printer to default state.
     * Should be called at the start of every print job.
     * 
     * @return ESC/POS initialization command bytes
     */
    fun initialize(): ByteArray
    
    /**
     * Set text alignment
     * 
     * @param alignment Alignment mode (LEFT, CENTER, RIGHT)
     * @return ESC/POS alignment command bytes
     */
    fun setAlignment(alignment: Alignment): ByteArray
    
    /**
     * Set text bold
     * 
     * @param enabled true for bold, false for normal weight
     * @return ESC/POS bold command bytes
     */
    fun setBold(enabled: Boolean): ByteArray
    
    /**
     * Set text size
     * 
     * @param size Text size mode (NORMAL, DOUBLE)
     * @return ESC/POS text size command bytes
     */
    fun setTextSize(size: TextSize): ByteArray
    
    /**
     * Add text line
     * 
     * Converts string to UTF-8 bytes with line feed.
     * 
     * @param text String to print
     * @return UTF-8 encoded text bytes with LF
     */
    fun addText(text: String): ByteArray
    
    /**
     * Feed paper
     * 
     * Advances paper by specified number of lines.
     * Used for spacing and cutting zone.
     * 
     * @param lines Number of lines to feed (1-255)
     * @return ESC/POS paper feed command bytes
     * @throws IllegalArgumentException if lines < 1 or > 255
     */
    fun feed(lines: Int): ByteArray
}
```

---

## Supporting Enums

```kotlin
enum class Alignment {
    LEFT,
    CENTER,
    RIGHT
}

enum class TextSize {
    NORMAL,
    DOUBLE
}
```

---

## Receipt Format Specification

The hardcoded receipt format from functional requirements:

```
================================     <- 32 equals, centered
      NAEEM DOCUMENTATION           <- Double size, bold, centered
================================     <- 32 equals, centered

Date: 2025-12-29 10:30              <- Normal, left-aligned

--------------------------------     <- 32 dashes, centered
Amount:              1234.56 SR     <- "Amount:" left, value right, bold

--------------------------------     <- 32 dashes, centered

Thank you for your business!        <- Normal, centered

[3 blank lines]                     <- Feed 3 lines for cutting
```

---

## Implementation Example

```kotlin
class ESCPOSReceiptFormatter : ReceiptFormatter {
    override fun format(receipt: Receipt): ByteArray {
        // Validate receipt
        receipt.validate().getOrThrow()
        
        // Build command sequence
        val commands = buildList {
            // Initialize
            add(initialize())
            
            // Top separator
            add(setAlignment(Alignment.CENTER))
            add(addText("================================"))
            
            // Business name
            add(setTextSize(TextSize.DOUBLE))
            add(setBold(true))
            add(addText(receipt.businessName))
            add(setTextSize(TextSize.NORMAL))
            add(setBold(false))
            
            // Bottom separator
            add(addText("================================"))
            add(addText("")) // Blank line
            
            // Date
            add(setAlignment(Alignment.LEFT))
            add(addText("Date: ${receipt.formattedTimestamp()}"))
            add(addText("")) // Blank line
            
            // Middle separator
            add(setAlignment(Alignment.CENTER))
            add(addText("--------------------------------"))
            
            // Amount (left label, right value)
            add(setAlignment(Alignment.LEFT))
            add(addText("Amount:"))
            add(setAlignment(Alignment.RIGHT))
            add(setBold(true))
            add(addText("${receipt.formattedAmount()} ${receipt.currency}"))
            add(setBold(false))
            
            // Bottom separator
            add(setAlignment(Alignment.CENTER))
            add(addText("--------------------------------"))
            add(addText("")) // Blank line
            
            // Thank you message
            add(addText("Thank you for your business!"))
            
            // Cutting zone
            add(feed(3))
        }
        
        // Flatten to single byte array
        return commands.fold(byteArrayOf()) { acc, bytes -> acc + bytes }
    }
    
    override fun initialize() = ESCPOSCommands.INIT
    
    override fun setAlignment(alignment: Alignment) = when (alignment) {
        Alignment.LEFT -> ESCPOSCommands.ALIGN_LEFT
        Alignment.CENTER -> ESCPOSCommands.ALIGN_CENTER
        Alignment.RIGHT -> ESCPOSCommands.ALIGN_RIGHT
    }
    
    override fun setBold(enabled: Boolean) = 
        if (enabled) ESCPOSCommands.BOLD_ON else ESCPOSCommands.BOLD_OFF
    
    override fun setTextSize(size: TextSize) = when (size) {
        TextSize.NORMAL -> ESCPOSCommands.SIZE_NORMAL
        TextSize.DOUBLE -> ESCPOSCommands.SIZE_DOUBLE
    }
    
    override fun addText(text: String) = 
        ESCPOSCommands.text(text) + ESCPOSCommands.LF
    
    override fun feed(lines: Int): ByteArray {
        require(lines in 1..255) { "Lines must be between 1 and 255" }
        return ESCPOSCommands.feed(lines)
    }
}
```

---

## Command Byte Reference

| Function | Hex Bytes | Description |
|----------|-----------|-------------|
| `initialize()` | `1B 40` | ESC @ - Reset printer |
| `setAlignment(LEFT)` | `1B 61 00` | ESC a 0 - Left align |
| `setAlignment(CENTER)` | `1B 61 01` | ESC a 1 - Center align |
| `setAlignment(RIGHT)` | `1B 61 02` | ESC a 2 - Right align |
| `setBold(true)` | `1B 45 01` | ESC E 1 - Bold on |
| `setBold(false)` | `1B 45 00` | ESC E 0 - Bold off |
| `setTextSize(NORMAL)` | `1D 21 00` | GS ! 0 - Normal size |
| `setTextSize(DOUBLE)` | `1D 21 11` | GS ! 17 - 2x width/height |
| `addText(text)` | `[UTF-8] 0A` | Text bytes + LF |
| `feed(n)` | `1B 64 [n]` | ESC d n - Feed n lines |

---

## Validation Rules

### Receipt Validation
Before formatting, validate:
- `amount > 0.0` → "Amount must be positive"
- `amount <= 999999.99` → "Amount too large"
- `businessName.isNotBlank()` → "Business name required"

### Parameter Validation
- `feed(lines)`: 1 ≤ lines ≤ 255
- `addText(text)`: Non-null string (empty allowed)

---

## Output Characteristics

**Typical Receipt Size**:
- Command bytes: ~180 bytes
- Text content: ~120 characters
- Total output: ~300 bytes

**Print Time**: <500ms for typical receipt (printer-dependent)

**Paper Usage**: ~8-10cm on 80mm paper roll

---

## Error Scenarios

| Scenario | Method | Exception | Message |
|----------|--------|-----------|---------|
| Negative amount | `format()` | IllegalArgumentException | "Amount must be positive" |
| Amount too large | `format()` | IllegalArgumentException | "Amount exceeds maximum" |
| Blank business name | `format()` | IllegalArgumentException | "Business name required" |
| Invalid feed lines | `feed()` | IllegalArgumentException | "Lines must be between 1 and 255" |

---

## Testing Requirements

### Unit Tests

1. **format() with valid receipt**
   - Verify output contains all ESC/POS commands in correct order
   - Check command sequence: INIT → alignment → text → feed
   - Validate byte array structure

2. **Individual command functions**
   - `initialize()` returns `[0x1B, 0x40]`
   - `setAlignment(CENTER)` returns `[0x1B, 0x61, 0x01]`
   - `setBold(true)` returns `[0x1B, 0x45, 0x01]`
   - `setTextSize(DOUBLE)` returns `[0x1D, 0x21, 0x11]`
   - `addText("Test")` contains UTF-8 "Test" + LF
   - `feed(3)` returns `[0x1B, 0x64, 0x03]`

3. **Validation tests**
   - `format()` with amount = 0 throws exception
   - `format()` with blank business name throws exception
   - `feed(0)` throws exception
   - `feed(256)` throws exception

### Integration Tests

1. **Full receipt generation**
   - Create receipt with amount 1500.50
   - Call `format(receipt)`
   - Verify output length ~300 bytes
   - Verify no corruption or missing commands

2. **Special characters**
   - Receipt with special chars in business name
   - Verify UTF-8 encoding correct
   - Test with printer (if available)

---

## Dependencies

- Domain models: `Receipt`, `Alignment`, `TextSize`
- Constants: `ESCPOSCommands` object
- Kotlin stdlib: `buildList`, string operations

---

## Performance Considerations

- Command generation is pure function (no I/O)
- Byte array allocation is minimal (~300 bytes)
- No dynamic memory allocation in hot path
- Safe to call on any dispatcher (no blocking operations)

---

## Future Enhancements (Out of Scope for v1)

- Custom business name parameter
- Optional logo/image support
- Configurable paper width (58mm vs 80mm)
- Barcode/QR code generation
- Multi-language support with character set commands

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2025-12-29 | Initial contract with hardcoded format |

---

**Contract Status**: ✅ APPROVED
