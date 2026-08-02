package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.DeviceRepository

class GetDeviceInfoUseCase(
    private val deviceRepository: DeviceRepository
) {
    operator fun invoke() = deviceRepository.getDeviceInfo()
}
