package com.jarvis.controller.data.repository

import com.jarvis.controller.data.db.dao.MessageDao
import com.jarvis.controller.data.db.entity.MessageEntity
import com.jarvis.controller.data.local.PreferencesManager
import com.jarvis.controller.data.remote.ChatCompletionRequest
import com.jarvis.controller.data.remote.ChatMessage
import com.jarvis.controller.data.remote.OpenAIService
import com.jarvis.controller.domain.model.AIResponse
import com.jarvis.controller.domain.model.Message
import com.jarvis.controller.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ChatRepositoryImpl(
    private val messageDao: MessageDao,
    private val openAIService: OpenAIService,
    private val preferencesManager: PreferencesManager
) : ChatRepository {

    override suspend fun sendMessage(message: Message): Result<AIResponse> {
        return try {
            val apiKey = kotlinx.coroutines.flow.first(
                preferencesManager.openaiApiKey
            ) ?: return Result.failure(Exception("API Key not configured"))

            val request = ChatCompletionRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    ChatMessage(role = "system", content = "You are JARVIS, a helpful AI assistant."),
                    ChatMessage(role = message.role, content = message.content)
                )
            )

            val response = openAIService.createChatCompletion(
                authHeader = "Bearer $apiKey",
                request = request
            )

            val responseContent = response.choices.firstOrNull()?.message?.content ?: ""
            val aiResponse = AIResponse(
                content = responseContent,
                model = response.model,
                timestamp = System.currentTimeMillis()
            )

            val assistantMessage = Message(
                id = System.currentTimeMillis().toString(),
                content = responseContent,
                role = "assistant",
                timestamp = System.currentTimeMillis()
            )
            saveMessage(assistantMessage)

            Result.success(aiResponse)
        } catch (e: Exception) {
            Timber.e(e, "Error sending message")
            Result.failure(e)
        }
    }

    override fun getConversationHistory(): Flow<List<Message>> {
        return messageDao.getAllMessages().map { entities ->
            entities.map { entity ->
                Message(
                    id = entity.id,
                    content = entity.content,
                    role = entity.role,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun saveMessage(message: Message) {
        val entity = MessageEntity(
            id = message.id,
            content = message.content,
            role = message.role,
            timestamp = message.timestamp
        )
        messageDao.insert(entity)
    }

    override suspend fun clearHistory() {
        messageDao.deleteAll()
    }
}

private suspend fun <T> kotlinx.coroutines.flow.Flow<T>.first(): T? {
    var result: T? = null
    kotlinx.coroutines.flow.collect { value ->
        result = value
    }
    return result
}
