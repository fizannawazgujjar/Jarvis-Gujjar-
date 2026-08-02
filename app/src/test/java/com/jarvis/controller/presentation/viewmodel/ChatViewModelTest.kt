package com.jarvis.controller.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.jarvis.controller.domain.model.Message
import com.jarvis.controller.domain.usecase.SendMessageUseCase
import com.jarvis.controller.domain.usecase.GetConversationHistoryUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ChatViewModelTest {
    private val sendMessageUseCase: SendMessageUseCase = mock()
    private val getConversationHistoryUseCase: GetConversationHistoryUseCase = mock()
    private val clearConversationUseCase: com.jarvis.controller.domain.usecase.ClearConversationUseCase = mock()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        whenever(getConversationHistoryUseCase()).thenReturn(flowOf(emptyList()))
        viewModel = ChatViewModel(
            sendMessageUseCase,
            getConversationHistoryUseCase,
            clearConversationUseCase
        )
    }

    @Test
    fun testUpdateInputText() = runTest {
        val testText = "Hello JARVIS"
        viewModel.updateInputText(testText)
        assert(viewModel.uiState.value.inputText == testText)
    }

    @Test
    fun testClearHistory() = runTest {
        viewModel.clearHistory()
        assert(viewModel.uiState.value.messages.isEmpty())
    }
}
