package com.naeem.thermalprinter.data.repository

import android.content.Context
import com.naeem.thermalprinter.domain.model.PrinterDevice

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
