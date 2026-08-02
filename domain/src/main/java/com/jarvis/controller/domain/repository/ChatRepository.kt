package com.jarvis.controller.domain.repository

import com.jarvis.controller.domain.model.Message
import com.jarvis.controller.domain.model.Conversation
import com.jarvis.controller.domain.model.AIResponse
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(message: Message): Result<AIResponse>
    fun getConversationHistory(): Flow<List<Message>>
    suspend fun saveMessage(message: Message)
    suspend fun clearHistory()
}
