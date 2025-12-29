package com.naeem.thermalprinter.domain.model

data class PrinterDevice(
    val name: String,
    val macAddress: String
) {
    fun isValidMacAddress(): Boolean {
        val macRegex = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$".toRegex()
        return macAddress.matches(macRegex)
    }

    fun displayName(): String = "$name ($macAddress)"
}
