package com.jarvis.controller.domain.model

data class Message(
    val id: String,
    val content: String,
    val role: String,
    val timestamp: Long
)

data class Conversation(
    val id: String,
    val title: String,
    val messages: List<Message>,
    val createdAt: Long,
    val updatedAt: Long
)

data class AIResponse(
    val content: String,
    val model: String,
    val timestamp: Long
)
