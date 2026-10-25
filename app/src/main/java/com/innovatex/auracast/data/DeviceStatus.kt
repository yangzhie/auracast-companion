package com.innovatex.auracast.data

// Holds the status of bluetooth, location and hearing permissions
data class DeviceStatus(
    val bluetoothOn: Boolean = false,
    val bluetoothGranted: Boolean = false,
    val locationGranted: Boolean = false,
) {
    // Permissions are granted, regardless of whether the radio is on
    val permissionsGranted: Boolean
        get() = bluetoothGranted && locationGranted

    // Bluetooth is usable: allowed and switched on
    val bluetoothReady: Boolean
        get() = bluetoothGranted && bluetoothOn

    val allReady: Boolean
        get() = bluetoothReady && locationGranted
}