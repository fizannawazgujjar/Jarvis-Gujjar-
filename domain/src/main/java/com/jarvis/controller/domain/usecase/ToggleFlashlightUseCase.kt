package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.DeviceRepository

class ToggleFlashlightUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(enable: Boolean): Result<Unit> {
        return deviceRepository.toggleFlashlight(enable)
    }
}
