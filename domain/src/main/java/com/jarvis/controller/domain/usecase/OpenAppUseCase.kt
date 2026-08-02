package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.DeviceRepository

class OpenAppUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(packageName: String): Result<Unit> {
        return deviceRepository.openApp(packageName)
    }
}
