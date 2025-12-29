package com.naeem.thermalprinter.domain.model

sealed class PrinterStatus {
    object Disconnected : PrinterStatus()
    object Connecting : PrinterStatus()
    data class Connected(val device: PrinterDevice) : PrinterStatus()

    fun toDisplayText(): String = when (this) {
        Disconnected -> "Disconnected"
        Connecting -> "Connecting..."
        is Connected -> "Connected: ${device.name}"
    }

    fun canPrint(): Boolean = this is Connected
}
