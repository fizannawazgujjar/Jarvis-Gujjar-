package com.jarvis.app.network

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ApiClient(private val baseUrl: String) {
    private val client = OkHttpClient()
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    suspend fun pairDevice(code: String): String = withContext(Dispatchers.IO) {
        val payload = JSONObject().put("code", code).toString()
        val req = Request.Builder()
            .url("$baseUrl/api/v1/auth/pair")
            .post(payload.toRequestBody(jsonMedia))
            .build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw Exception("pairing failed: ${resp.code}")
            return@withContext resp.body?.string() ?: ""
        }
    }

    suspend fun sendMessage(userId: String, prompt: String): String = withContext(Dispatchers.IO) {
        val payload = JSONObject().put("user_id", userId).put("prompt", prompt).toString()
        val req = Request.Builder()
            .url("$baseUrl/api/v1/commander/ask")
            .post(payload.toRequestBody(jsonMedia))
            .build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw Exception("send failed: ${resp.code}")
            return@withContext resp.body?.string() ?: ""
        }
    }
}
