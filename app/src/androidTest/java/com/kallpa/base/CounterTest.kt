package com.kallpa.base

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class CounterTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initial_state_isDisplayed() {
        composeTestRule.setContent {
            MaterialTheme {
                CounterScreen()
            }
        }

        composeTestRule.onNodeWithText("Hola, KallpaBase!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clics: 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Incrementar").assertIsDisplayed()
    }

    @Test
    fun click_increments_count() {
        composeTestRule.setContent {
            MaterialTheme {
                CounterScreen()
            }
        }

        composeTestRule.onNodeWithText("Incrementar").performClick()
        composeTestRule.onNodeWithText("Clics: 1").assertIsDisplayed()
    }

    @Test
    fun multiple_clicks_update_count() {
        composeTestRule.setContent {
            MaterialTheme {
                CounterScreen()
            }
        }

        repeat(3) {
            composeTestRule.onNodeWithText("Incrementar").performClick()
        }
        composeTestRule.onNodeWithText("Clics: 3").assertIsDisplayed()
    }
}
