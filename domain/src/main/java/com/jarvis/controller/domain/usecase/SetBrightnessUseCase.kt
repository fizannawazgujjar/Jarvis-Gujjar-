package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.DeviceRepository

class SetBrightnessUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(level: Int): Result<Unit> {
        return deviceRepository.setBrightness(level.coerceIn(0, 255))
    }
}
