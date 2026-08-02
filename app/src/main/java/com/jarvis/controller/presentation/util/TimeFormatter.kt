package com.jarvis.controller.presentation.util

import kotlin.math.abs

object TimeFormatter {
    fun formatTimestamp(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diffMs = currentTime - timestamp
        val diffSeconds = diffMs / 1000
        val diffMinutes = diffSeconds / 60
        val diffHours = diffMinutes / 60
        val diffDays = diffHours / 24

        return when {
            diffSeconds < 60 -> "just now"
            diffMinutes < 60 -> "${diffMinutes}m ago"
            diffHours < 24 -> "${diffHours}h ago"
            diffDays < 7 -> "${diffDays}d ago"
            else -> {
                val date = java.util.Date(timestamp)
                java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(date)
            }
        }
    }
}
