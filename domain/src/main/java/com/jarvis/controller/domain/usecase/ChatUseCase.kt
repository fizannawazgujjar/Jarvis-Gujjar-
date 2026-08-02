package com.jarvis.controller.domain.usecase

import com.jarvis.controller.domain.model.Message
import com.jarvis.controller.domain.model.AIResponse
import com.jarvis.controller.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class SendMessageUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(content: String): Result<AIResponse> {
        val message = Message(
            id = System.currentTimeMillis().toString(),
            content = content,
            role = "user",
            timestamp = System.currentTimeMillis()
        )
        chatRepository.saveMessage(message)
        return chatRepository.sendMessage(message)
    }
}

class GetConversationHistoryUseCase(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<List<Message>> {
        return chatRepository.getConversationHistory()
    }
}

class ClearConversationUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke() {
        chatRepository.clearHistory()
    }
}
