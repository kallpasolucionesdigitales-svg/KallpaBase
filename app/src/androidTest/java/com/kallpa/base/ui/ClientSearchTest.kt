package com.kallpa.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kallpa.base.domain.Client
import com.kallpa.base.testing.FakeClientRepository
import com.kallpa.base.ui.clients.ClientsScreen
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ClientSearchTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun repositoryWithThreeClients(): FakeClientRepository {
        val repository = FakeClientRepository()
        runBlocking {
            repository.save(Client(id = "1", nombre = "Ana Pérez"))
            repository.save(Client(id = "2", nombre = "Luis Gómez"))
            repository.save(Client(id = "3", nombre = "Marta Ruiz"))
        }
        return repository
    }

    @Test
    fun listadoInicial_conBuscarHabilitado() {
        val repository = repositoryWithThreeClients()

        composeTestRule.setContent {
            MaterialTheme {
                MainScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Luis Gómez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Marta Ruiz").assertIsDisplayed()
        val tops = listOf("Ana Pérez", "Luis Gómez", "Marta Ruiz").map { name ->
            composeTestRule.onNodeWithText(name).fetchSemanticsNode().boundsInRoot.top
        }
        assertTrue(
            "El listado debe conservar el orden Ana, Luis, Marta",
            tops[0] < tops[1] && tops[1] < tops[2],
        )
        composeTestRule.onNodeWithText("Nuevo cliente").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Ver ficha").assertCountEquals(3)
        composeTestRule.onAllNodesWithText("Editar").assertCountEquals(3)
        composeTestRule.onAllNodesWithText("Eliminar").assertCountEquals(3)
        composeTestRule
            .onNodeWithContentDescription("Buscar clientes")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun buscar_muestraCampoYConservaDestino() {
        val repository = repositoryWithThreeClients()

        composeTestRule.setContent {
            MaterialTheme {
                MainScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Buscar cliente").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Buscar clientes").performClick()

        composeTestRule.onNodeWithContentDescription("Título, Clientes").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Buscar cliente").assertIsFocused()
        composeTestRule
            .onNodeWithContentDescription("Sincronizar, no disponible")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun busquedaPorNombre_filtra() {
        val repository = repositoryWithThreeClients()

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Buscar cliente").performTextInput("ana")

        composeTestRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Luis Gómez").assertDoesNotExist()
        composeTestRule.onNodeWithText("Marta Ruiz").assertDoesNotExist()
    }

    @Test
    fun sinCoincidencias_muestraMensajeSinResultados() {
        val repository = repositoryWithThreeClients()

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Buscar cliente").performTextInput("zzz")

        composeTestRule.onNodeWithText("Sin resultados").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sin clientes").assertDoesNotExist()
        composeTestRule.onNodeWithText("Nuevo cliente").assertIsDisplayed()
    }

    @Test
    fun limpiar_restauraListado() {
        val repository = repositoryWithThreeClients()

        composeTestRule.setContent {
            MaterialTheme {
                ClientsScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Buscar cliente").performTextInput("ana")
        composeTestRule.onNodeWithText("Luis Gómez").assertDoesNotExist()
        composeTestRule.onNodeWithText("Limpiar").performClick()

        val queryValue = composeTestRule.onNodeWithContentDescription("Buscar cliente")
            .fetchSemanticsNode().config[SemanticsProperties.EditableText].text
        assertEquals("", queryValue)
        composeTestRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Luis Gómez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Marta Ruiz").assertIsDisplayed()
    }
}
