package com.kallpa.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kallpa.base.domain.Client
import com.kallpa.base.testing.FakeClientRepository
import com.kallpa.base.ui.clients.ClientsScreen
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ClientDeleteTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cancelar_noElimina() = runBlocking {
        val repository = FakeClientRepository()
        repository.save(Client(id = "cliente-1", nombre = "Ana Pérez"))

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Eliminar").performClick()
        composeTestRule.onNodeWithText("¿Confirmar la baja de este cliente?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancelar").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        assertEquals(1, repository.list().size)
    }

    @Test
    fun confirmar_eliminaDeLista() = runBlocking {
        val repository = FakeClientRepository()
        repository.save(Client(id = "cliente-1", nombre = "Ana Pérez"))

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Eliminar").performClick()
        composeTestRule.onNodeWithText("Confirmar").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Sin clientes").assertIsDisplayed()
        assertTrue(repository.list().isEmpty())
    }
}
