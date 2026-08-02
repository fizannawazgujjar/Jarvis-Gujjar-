package com.jarvis.controller.data.repository

import com.jarvis.controller.domain.model.Message
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChatRepositoryTest {

    @Test
    fun testMessageCreation() = runTest {
        val message = Message(
            id = "1",
            content = "Test message",
            role = "user",
            timestamp = System.currentTimeMillis()
        )
        assert(message.id == "1")
        assert(message.content == "Test message")
        assert(message.role == "user")
    }

    @Test
    fun testMessageComparison() = runTest {
        val message1 = Message("1", "Hello", "user", 1000)
        val message2 = Message("1", "Hello", "user", 1000)
        assert(message1 == message2)
    }
}
