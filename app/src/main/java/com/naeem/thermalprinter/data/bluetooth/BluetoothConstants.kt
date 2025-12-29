package com.naeem.thermalprinter.data.bluetooth

import java.util.UUID

object BluetoothConstants {
    val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    const val CONNECTION_TIMEOUT_MS = 5000L
    const val SOCKET_BUFFER_SIZE = 1024
}
