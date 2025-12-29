package com.naeem.thermalprinter.domain.printer

enum class TextSize(val command: ByteArray) {
    NORMAL(byteArrayOf(0x1D, 0x21, 0x00)),
    DOUBLE(byteArrayOf(0x1D, 0x21, 0x11)),
    LARGE(byteArrayOf(0x1D, 0x21, 0x22))
}
