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
class DeviceControlScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun testDeviceScreenDisplaysTitle() {
        composeTestRule.setContent {
            DeviceControlScreen(navController = navController)
        }
        composeTestRule.onNodeWithText("Device Controls").assertExists()
    }

    @Test
    fun testDeviceScreenDisplaysControls() {
        composeTestRule.setContent {
            DeviceControlScreen(navController = navController)
        }
        composeTestRule.onNodeWithText("Flashlight").assertExists()
        composeTestRule.onNodeWithText("Battery").assertExists()
        composeTestRule.onNodeWithText("Wi-Fi").assertExists()
    }
}
