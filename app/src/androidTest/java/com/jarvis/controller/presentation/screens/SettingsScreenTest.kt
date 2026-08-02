package com.jarvis.controller.presentation.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun testSettingsScreenDisplaysTitle() {
        composeTestRule.setContent {
            SettingsScreen(navController = navController)
        }
        composeTestRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun testSettingsScreenDisplaysCategories() {
        composeTestRule.setContent {
            SettingsScreen(navController = navController)
        }
        composeTestRule.onNodeWithText("Voice Settings").assertExists()
        composeTestRule.onNodeWithText("Theme Settings").assertExists()
    }
}
