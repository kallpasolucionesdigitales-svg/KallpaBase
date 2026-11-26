package com.kallpa.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.kallpa.base.testing.FakeClientRepository
import com.kallpa.base.ui.clients.ClientsScreen
import org.junit.Rule
import org.junit.Test

class ClientListTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun listaVacia_muestraEstadoVacio() {
        val repository = FakeClientRepository()

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Clientes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sin clientes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nuevo cliente").assertIsDisplayed()
    }
}
