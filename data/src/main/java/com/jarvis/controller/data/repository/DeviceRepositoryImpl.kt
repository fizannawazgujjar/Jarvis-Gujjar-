package com.jarvis.controller.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.jarvis.controller.domain.model.DeviceInfo
import com.jarvis.controller.domain.model.PermissionStatus
import com.jarvis.controller.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class DeviceRepositoryImpl(
    private val context: Context
) : DeviceRepository {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    override fun getDeviceInfo(): Flow<DeviceInfo> = flow {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val batteryHealth = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_HEALTH)

        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory

        val deviceInfo = DeviceInfo(
            batteryLevel = batteryLevel,
            batteryHealth = when (batteryHealth) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                else -> "Unknown"
            },
            storageUsed = 0L,
            storageFree = 0L,
            ramUsed = usedMemory,
            ramTotal = totalMemory,
            wifiConnected = isWifiConnected(),
            bluetoothOn = isBluetoothOn(),
            mobileDataOn = isMobileDataEnabled()
        )
        emit(deviceInfo)
    }

    override fun getPermissionStatus(permission: String): Flow<PermissionStatus> = flow {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        emit(PermissionStatus(permission, isGranted))
    }

    override suspend fun toggleFlashlight(enable: Boolean): Result<Unit> {
        return try {
            val cameraId = cameraManager.cameraIdList[0]
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, enable)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling flashlight")
            Result.failure(e)
        }
    }

    override suspend fun setVolume(level: Int): Result<Unit> {
        return try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error setting volume")
            Result.failure(e)
        }
    }

    override suspend fun setBrightness(level: Int): Result<Unit> {
        return try {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, level)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error setting brightness")
            Result.failure(e)
        }
    }

    override suspend fun openApp(packageName: String): Result<Unit> {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                ?: return Result.failure(Exception("Package not found"))
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error opening app")
            Result.failure(e)
        }
    }

    private fun isWifiConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun isBluetoothOn(): Boolean {
        val bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.isEnabled == true
    }

    private fun isMobileDataEnabled(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}
