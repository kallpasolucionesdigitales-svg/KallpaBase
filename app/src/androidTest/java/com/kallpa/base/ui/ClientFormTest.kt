package com.kallpa.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kallpa.base.testing.FakeClientRepository
import com.kallpa.base.ui.clients.ClientsScreen
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ClientFormTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun altaValida_actualizaLista() = runBlocking {
        val repository = FakeClientRepository()

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Nuevo cliente").performClick()
        composeTestRule.onNodeWithText("Nombre").performTextInput("Ana Pérez")
        composeTestRule.onNodeWithText("Teléfono").performTextInput("+51 999 000 111")
        composeTestRule.onNodeWithText("Email").performTextInput("ana@example.com")
        composeTestRule.onNodeWithText("Dirección").performTextInput("Lima")
        composeTestRule.onNodeWithText("Guardar").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        composeTestRule.onNodeWithText("ana@example.com").assertIsDisplayed()
        val saved = repository.list().single()
        assertTrue(saved.id.isNotBlank())
        assertEquals("Ana Pérez", saved.nombre)
        assertEquals("Lima", saved.direccion)
    }

    @Test
    fun camposInvalidos_muestraErrores() = runBlocking {
        val repository = FakeClientRepository()

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Nuevo cliente").performClick()
        composeTestRule.onNodeWithText("Guardar").performClick()
        composeTestRule.onNodeWithText("El nombre es obligatorio").assertIsDisplayed()

        composeTestRule.onNodeWithText("Nombre").performTextInput("Ana Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("ana")
        composeTestRule.onNodeWithText("Guardar").performClick()
        composeTestRule.onNodeWithText("El email no es válido").assertIsDisplayed()
        assertTrue(repository.list().isEmpty())
    }
}
