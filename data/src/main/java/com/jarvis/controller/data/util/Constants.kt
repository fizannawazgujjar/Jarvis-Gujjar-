package com.jarvis.controller.data.util

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val DATABASE_NAME = "jarvis_database"
    const val PREFERENCES_NAME = "jarvis_prefs"

    object Keys {
        val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val SELECTED_AI_PROVIDER = stringPreferencesKey("selected_ai_provider")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val VOICE_LANGUAGE = stringPreferencesKey("voice_language")
    }

    object AI {
        const val DEFAULT_MODEL = "gpt-3.5-turbo"
        const val GEMINI_MODEL = "gemini-pro"
        const val DEFAULT_TEMPERATURE = 0.7
        const val DEFAULT_MAX_TOKENS = 2048
    }
}
