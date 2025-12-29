package com.naeem.thermalprinter.domain.printer

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
