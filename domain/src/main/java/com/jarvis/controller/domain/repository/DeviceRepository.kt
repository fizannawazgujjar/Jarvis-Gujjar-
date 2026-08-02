package com.jarvis.controller.domain.repository

import com.jarvis.controller.domain.model.DeviceInfo
import com.jarvis.controller.domain.model.PermissionStatus
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDeviceInfo(): Flow<DeviceInfo>
    fun getPermissionStatus(permission: String): Flow<PermissionStatus>
    suspend fun toggleFlashlight(enable: Boolean): Result<Unit>
    suspend fun setVolume(level: Int): Result<Unit>
    suspend fun setBrightness(level: Int): Result<Unit>
    suspend fun openApp(packageName: String): Result<Unit>
}
