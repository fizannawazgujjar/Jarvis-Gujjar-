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
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun testHomeScreenDisplaysTitle() {
        composeTestRule.setContent {
            HomeScreen(navController = navController)
        }
        composeTestRule.onNodeWithText("JARVIS").assertExists()
    }

    @Test
    fun testHomeScreenDisplaysWelcomeText() {
        composeTestRule.setContent {
            HomeScreen(navController = navController)
        }
        composeTestRule.onNodeWithText("Ready to assist").assertExists()
    }
}
