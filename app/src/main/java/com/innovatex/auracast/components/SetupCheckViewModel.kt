package com.innovatex.auracast.components

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.innovatex.auracast.data.DeviceStatus

class SetupCheckViewModel : ViewModel() {
    var status by mutableStateOf(DeviceStatus())
        private set

    fun refresh(context: Context) {
        fun granted(permission: String) =
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

        // isEnabled needs no permission, so this is safe to read before granting
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter

        status = DeviceStatus(
            bluetoothGranted = granted(Manifest.permission.BLUETOOTH_CONNECT) && granted(Manifest.permission.BLUETOOTH_SCAN),
            bluetoothOn = adapter?.isEnabled == true,
            locationGranted = granted(Manifest.permission.ACCESS_FINE_LOCATION)
        )
    }
}