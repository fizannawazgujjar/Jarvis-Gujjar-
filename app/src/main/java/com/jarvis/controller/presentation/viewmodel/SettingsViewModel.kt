package com.jarvis.controller.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SettingsUiState(
    val aiProvider: String = "OpenAI",
    val theme: String = "Dark",
    val language: String = "English",
    val voiceEnabled: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setAiProvider(provider: String) {
        _uiState.value = _uiState.value.copy(aiProvider = provider)
        viewModelScope.launch {
            try {
                Timber.d("AI Provider set to: $provider")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
                Timber.e(e, "Error setting AI provider")
            }
        }
    }

    fun setTheme(theme: String) {
        _uiState.value = _uiState.value.copy(theme = theme)
        viewModelScope.launch {
            try {
                Timber.d("Theme set to: $theme")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
                Timber.e(e, "Error setting theme")
            }
        }
    }

    fun setLanguage(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
        viewModelScope.launch {
            try {
                Timber.d("Language set to: $language")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
                Timber.e(e, "Error setting language")
            }
        }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(voiceEnabled = enabled)
        viewModelScope.launch {
            try {
                Timber.d("Voice enabled: $enabled")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
                Timber.e(e, "Error setting voice")
            }
        }
    }
}
