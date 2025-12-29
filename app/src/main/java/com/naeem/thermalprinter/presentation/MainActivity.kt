package com.naeem.thermalprinter.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(BluetoothManager::class.java)?.adapter
        } else {
            BluetoothAdapter.getDefaultAdapter()
        }
    }
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(
                this,
                "Bluetooth permission is required for printer connection",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkBluetoothAndPermissions()
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
    
    private fun checkBluetoothAndPermissions() {
        if (bluetoothAdapter == null) {
            Toast.makeText(
                this,
                "Bluetooth is not supported on this device",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        
        if (bluetoothAdapter?.isEnabled == false) {
            Toast.makeText(
                this,
                "Please enable Bluetooth to use this app",
                Toast.LENGTH_LONG
            ).show()
        }
        
        if (!hasBluetoothPermissions()) {
            requestBluetoothPermissions()
        }
    }
    
    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
        requestPermissionLauncher.launch(permissions)
    }
}

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Naeem Documentation",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Thermal Printer App v1.0",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Ready for printing",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
