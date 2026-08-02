package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.DeviceRepository

class SetVolumeUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(level: Int): Result<Unit> {
        return deviceRepository.setVolume(level.coerceIn(0, 15))
    }
}
