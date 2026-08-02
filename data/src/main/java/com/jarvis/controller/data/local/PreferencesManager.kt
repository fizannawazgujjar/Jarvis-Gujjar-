package com.jarvis.controller.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        private val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        private val SELECTED_AI_PROVIDER = stringPreferencesKey("selected_ai_provider")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val VOICE_LANGUAGE = stringPreferencesKey("voice_language")
    }

    val openaiApiKey: Flow<String?> = dataStore.data.map { it[OPENAI_API_KEY] }
    val geminiApiKey: Flow<String?> = dataStore.data.map { it[GEMINI_API_KEY] }
    val selectedAiProvider: Flow<String?> = dataStore.data.map { it[SELECTED_AI_PROVIDER] }
    val themeMode: Flow<String?> = dataStore.data.map { it[THEME_MODE] }
    val voiceLanguage: Flow<String?> = dataStore.data.map { it[VOICE_LANGUAGE] }

    suspend fun setOpenaiApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[OPENAI_API_KEY] = key
        }
    }

    suspend fun setGeminiApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[GEMINI_API_KEY] = key
        }
    }

    suspend fun setSelectedAiProvider(provider: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_AI_PROVIDER] = provider
        }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    suspend fun setVoiceLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[VOICE_LANGUAGE] = language
        }
    }
}
