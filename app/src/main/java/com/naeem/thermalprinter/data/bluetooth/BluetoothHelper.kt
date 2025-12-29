package com.naeem.thermalprinter.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import com.naeem.thermalprinter.domain.model.PrinterDevice
import com.naeem.thermalprinter.domain.model.PrinterStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream

class BluetoothHelper(private val context: Context) {
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private val bluetoothAdapter: BluetoothAdapter? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(BluetoothManager::class.java)?.adapter
        } else {
            BluetoothAdapter.getDefaultAdapter()
        }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getPairedDevices(): List<PrinterDevice> {
        return try {
            bluetoothAdapter?.bondedDevices?.map { device ->
                PrinterDevice(device.name ?: "Unknown", device.address)
            } ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connect(macAddress: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val device = bluetoothAdapter?.getRemoteDevice(macAddress)
                ?: return@withContext Result.failure(Exception("Bluetooth adapter not available"))

            socket = device.createRfcommSocketToServiceRecord(BluetoothConstants.SPP_UUID)
            socket?.connect()
            outputStream = socket?.outputStream

            Result.success(Unit)
        } catch (e: IOException) {
            socket?.close()
            socket = null
            outputStream = null
            Result.failure(e)
        } catch (e: SecurityException) {
            Result.failure(e)
        }
    }

    suspend fun send(data: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val out = outputStream ?: return@withContext Result.failure(Exception("Not connected"))
            out.write(data)
            out.flush()
            Result.success(Unit)
        } catch (e: IOException) {
            disconnect()
            Result.failure(e)
        }
    }

    fun disconnect() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            // Ignore
        } finally {
            outputStream = null
            socket = null
        }
    }

    fun isConnected(): Boolean = socket?.isConnected == true

    fun getStatus(): PrinterStatus {
        return if (isConnected()) {
            // Get the connected device info
            val device = socket?.remoteDevice
            if (device != null) {
                PrinterStatus.Connected(PrinterDevice(device.name ?: "Unknown", device.address))
            } else {
                PrinterStatus.Disconnected
            }
        } else {
            PrinterStatus.Disconnected
        }
    }
}
