package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.model.DeviceInfo
import com.jarvis.controller.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow

class GetDeviceInfoUseCase(
    private val deviceRepository: DeviceRepository
) {
    operator fun invoke(): Flow<DeviceInfo> {
        return deviceRepository.getDeviceInfo()
    }
}

class ToggleFlashlightUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(enable: Boolean): Result<Unit> {
        return deviceRepository.toggleFlashlight(enable)
    }
}

class SetVolumeUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(level: Int): Result<Unit> {
        return deviceRepository.setVolume(level.coerceIn(0, 15))
    }
}

class SetBrightnessUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(level: Int): Result<Unit> {
        return deviceRepository.setBrightness(level.coerceIn(0, 255))
    }
}

class OpenAppUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(packageName: String): Result<Unit> {
        return deviceRepository.openApp(packageName)
    }
}
