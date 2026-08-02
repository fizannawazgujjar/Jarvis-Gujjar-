package com.jarvis.controller.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import timber.log.Timber

class VoiceAssistantService : Service() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_LISTENING -> startListening()
            ACTION_STOP_LISTENING -> stopListening()
            else -> Timber.d("Unknown action")
        }
        return START_STICKY
    }

    private fun startListening() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        }
        isListening = true
        Timber.d("Voice assistant listening started")
    }

    private fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
        Timber.d("Voice assistant listening stopped")
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START_LISTENING = "com.jarvis.controller.START_LISTENING"
        const val ACTION_STOP_LISTENING = "com.jarvis.controller.STOP_LISTENING"
    }
}
