package com.kallpa.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.kallpa.base.domain.Client
import com.kallpa.base.testing.FakeClientRepository
import com.kallpa.base.ui.clients.ClientsScreen
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ClientEditTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun edicion_actualizaLista() = runBlocking {
        val repository = FakeClientRepository()
        repository.save(
            Client(
                id = "cliente-1",
                nombre = "Ana Pérez",
                telefono = "+51 999 000 111",
                email = "ana@example.com",
                direccion = "Lima",
            ),
        )

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Editar").performClick()

        val telefono = composeTestRule.onNodeWithText("Teléfono", substring = true)
            .fetchSemanticsNode().config.get(SemanticsProperties.EditableText).text
        val direccion = composeTestRule.onNodeWithText("Dirección", substring = true)
            .fetchSemanticsNode().config.get(SemanticsProperties.EditableText).text
        assertEquals("+51 999 000 111", telefono)
        assertEquals("Lima", direccion)

        composeTestRule.onNodeWithText("Teléfono", substring = true)
            .performTextReplacement("+51 987 654 321")
        composeTestRule.onNodeWithText("Guardar").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("+51 987 654 321").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        val saved = repository.list().single()
        assertEquals("cliente-1", saved.id)
        assertEquals("+51 987 654 321", saved.telefono)
    }
}
