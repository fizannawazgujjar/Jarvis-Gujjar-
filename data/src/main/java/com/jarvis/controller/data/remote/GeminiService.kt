package com.jarvis.controller.data.remote

import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header

data class GeminiContent(
    val parts: List<TextPart>
)

data class TextPart(
    val text: String
)

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String
)

interface GeminiService {
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
