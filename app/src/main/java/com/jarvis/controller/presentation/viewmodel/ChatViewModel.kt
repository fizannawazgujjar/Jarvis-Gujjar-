package com.jarvis.controller.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.controller.domain.model.Message
import com.jarvis.controller.domain.usecase.SendMessageUseCase
import com.jarvis.controller.domain.usecase.GetConversationHistoryUseCase
import com.jarvis.controller.domain.usecase.ClearConversationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getConversationHistoryUseCase: GetConversationHistoryUseCase,
    private val clearConversationUseCase: ClearConversationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadConversationHistory()
    }

    private fun loadConversationHistory() {
        viewModelScope.launch {
            getConversationHistoryUseCase().collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null,
            inputText = ""
        )

        viewModelScope.launch {
            try {
                val result = sendMessageUseCase(content)
                result.onSuccess { response ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    Timber.d("Message sent successfully: ${response.content}")
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                    Timber.e(exception, "Error sending message")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
                Timber.e(e, "Exception sending message")
            }
        }
    }

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                clearConversationUseCase()
                _uiState.value = _uiState.value.copy(messages = emptyList())
            } catch (e: Exception) {
                Timber.e(e, "Error clearing history")
            }
        }
    }
}
