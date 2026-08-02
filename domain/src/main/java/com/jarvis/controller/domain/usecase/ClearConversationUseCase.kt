package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.ChatRepository

class ClearConversationUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke() = chatRepository.clearHistory()
}
