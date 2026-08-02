package com.jarvis.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.jarvis.app.network.ApiClient

class MainViewModel: ViewModel() {
    private val api = ApiClient("http://10.0.2.2:8000")
    var messages = mutableListOf<String>()
        private set
    var userId: String = "local-user"

    fun pairDevice(code: String, onResult: (Boolean, String)->Unit) {
        viewModelScope.launch {
            try {
                val r = api.pairDevice(code)
                withContext(Dispatchers.Main) {
                    onResult(true, r)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, e.message ?: "error")
                }
            }
        }
    }

    fun sendMessage(text: String) {
        messages.add("Me: $text")
        viewModelScope.launch {
            try {
                val resp = api.sendMessage(userId, text)
                withContext(Dispatchers.Main) {
                    messages.add("Jarvis: $resp")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    messages.add("ERROR: ${e.message}")
                }
            }
        }
    }
}
