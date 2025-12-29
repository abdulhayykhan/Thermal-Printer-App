package com.naeem.thermalprinter.domain.printer

enum class Alignment(val command: ByteArray) {
    LEFT(byteArrayOf(0x1B, 0x61, 0x00)),
    CENTER(byteArrayOf(0x1B, 0x61, 0x01)),
    RIGHT(byteArrayOf(0x1B, 0x61, 0x02))
}
