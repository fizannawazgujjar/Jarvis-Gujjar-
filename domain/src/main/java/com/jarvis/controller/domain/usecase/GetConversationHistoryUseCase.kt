package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.ChatRepository

class GetConversationHistoryUseCase(
    private val chatRepository: ChatRepository
) {
    operator fun invoke() = chatRepository.getConversationHistory()
}
