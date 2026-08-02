package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.repository.ChatRepository

class SendMessageUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(content: String) = chatRepository.sendMessage(
        com.jarvis.controller.domain.model.Message(
            id = System.currentTimeMillis().toString(),
            content = content,
            role = "user",
            timestamp = System.currentTimeMillis()
        )
    )
}
