package com.kallpa.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
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

class ClientDetailTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fichaMuestraDatosYAbreEdicion() = runBlocking {
        val repository = FakeClientRepository()
        repository.save(
            Client(
                id = "cliente-1",
                nombre = "Ana Pérez",
                telefono = "+51 999 000 111",
                email = "ana@example.com",
                direccion = "Av. Lima 123",
            ),
        )

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Ver ficha").performClick()

        composeTestRule.onNodeWithText("Ficha de cliente").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Ana Pérez").assertCountEquals(2)
        composeTestRule.onAllNodesWithText("+51 999 000 111").assertCountEquals(2)
        composeTestRule.onAllNodesWithText("ana@example.com").assertCountEquals(2)
        composeTestRule.onAllNodesWithText("Av. Lima 123").assertCountEquals(2)
        composeTestRule.onNodeWithContentDescription("Cerrar ficha").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Editar cliente").performClick()

        val nombre = composeTestRule.onNodeWithText("Nombre", substring = true)
            .fetchSemanticsNode().config[SemanticsProperties.EditableText].text
        assertEquals("Ana Pérez", nombre)

        composeTestRule.onNodeWithText("Teléfono", substring = true)
            .performTextReplacement("+51 987 654 321")
        composeTestRule.onNodeWithText("Guardar").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("+51 987 654 321").assertIsDisplayed()
        val saved = repository.list().single()
        assertEquals("cliente-1", saved.id)
        assertEquals("+51 987 654 321", saved.telefono)
    }
}
