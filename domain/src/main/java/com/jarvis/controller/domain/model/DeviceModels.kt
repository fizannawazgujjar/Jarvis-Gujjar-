package com.jarvis.controller.domain.model

data class DeviceInfo(
    val batteryLevel: Int,
    val batteryHealth: String,
    val storageUsed: Long,
    val storageFree: Long,
    val ramUsed: Long,
    val ramTotal: Long,
    val wifiConnected: Boolean,
    val bluetoothOn: Boolean,
    val mobileDataOn: Boolean
)

data class PermissionStatus(
    val permission: String,
    val isGranted: Boolean
)
