package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.ChatRepository
import com.jarvis.controller.domain.repository.DeviceRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock

class ChatUseCaseTest {
    private val chatRepository: ChatRepository = mock()

    @Test
    fun testSendMessageUseCaseExists() = runTest {
        val useCase = SendMessageUseCase(chatRepository)
        assert(useCase != null)
    }

    @Test
    fun testGetConversationHistoryUseCaseExists() = runTest {
        val useCase = GetConversationHistoryUseCase(chatRepository)
        assert(useCase != null)
    }
}

class DeviceUseCaseTest {
    private val deviceRepository: DeviceRepository = mock()

    @Test
    fun testToggleFlashlightUseCaseExists() = runTest {
        val useCase = ToggleFlashlightUseCase(deviceRepository)
        assert(useCase != null)
    }

    @Test
    fun testSetVolumeUseCaseExists() = runTest {
        val useCase = SetVolumeUseCase(deviceRepository)
        assert(useCase != null)
    }
}
