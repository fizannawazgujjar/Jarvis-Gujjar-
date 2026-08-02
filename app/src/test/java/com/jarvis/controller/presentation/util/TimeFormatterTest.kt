package com.jarvis.controller.presentation.util

import org.junit.Test

class TimeFormatterTest {

    @Test
    fun testFormatJustNow() {
        val current = System.currentTimeMillis()
        val result = TimeFormatter.formatTimestamp(current)
        assert(result == "just now")
    }

    @Test
    fun testFormatMinutesAgo() {
        val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
        val result = TimeFormatter.formatTimestamp(fiveMinutesAgo)
        assert(result.contains("m ago"))
    }
}
