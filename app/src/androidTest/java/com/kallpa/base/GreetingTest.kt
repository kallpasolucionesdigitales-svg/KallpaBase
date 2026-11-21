package com.kallpa.base

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class GreetingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun greeting_isDisplayed() {
        composeTestRule.setContent {
            Greeting("KallpaBase")
        }

        composeTestRule.onNodeWithText("Hola, KallpaBase!").assertIsDisplayed()
    }
}
