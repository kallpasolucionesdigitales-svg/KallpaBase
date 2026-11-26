package com.kallpa.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.kallpa.base.testing.FakeClientRepository
import org.junit.Rule
import org.junit.Test

class MainMenuTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menuInicial_muestraCuatroDestinos() {
        val repository = FakeClientRepository()

        composeTestRule.setContent {
            MaterialTheme {
                MainScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        MainDestination.entries.forEach { destination ->
            composeTestRule
                .onNodeWithContentDescription(destination.contentDescription)
                .assertIsDisplayed()
        }
        composeTestRule
            .onNodeWithContentDescription(MainDestination.CLIENTS.contentDescription)
            .assertIsSelected()
        composeTestRule.onNodeWithText("Sin clientes").assertIsDisplayed()
    }

    @Test
    fun destinoDeshabilitado_noNavega() {
        val repository = FakeClientRepository()

        composeTestRule.setContent {
            MaterialTheme {
                MainScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        MainDestination.entries.filter { !it.enabled }.forEach { destination ->
            composeTestRule
                .onNodeWithContentDescription(destination.contentDescription)
                .performTouchInput { click() }
            composeTestRule
                .onNodeWithContentDescription(MainDestination.CLIENTS.contentDescription)
                .assertIsSelected()
            composeTestRule.onNodeWithText("Sin clientes").assertIsDisplayed()
        }
    }

    @Test
    fun destinosFuturos_visiblesYDeshabilitados() {
        val repository = FakeClientRepository()

        composeTestRule.setContent {
            MaterialTheme {
                MainScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        MainDestination.entries.filter { !it.enabled }.forEach { destination ->
            composeTestRule
                .onNodeWithContentDescription(destination.contentDescription)
                .assertIsDisplayed()
                .assertIsNotEnabled()
        }
        composeTestRule.onAllNodesWithText("Próximamente").assertCountEquals(2)
        composeTestRule.onNodeWithText("Fase 2").assertIsDisplayed()
    }

    @Test
    fun barraSuperior_estadoInicial() {
        val repository = FakeClientRepository()

        composeTestRule.setContent {
            MaterialTheme {
                MainScreen(repository = repository)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Título, Clientes").assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Buscar, no disponible")
            .assertIsDisplayed()
            .assertIsNotEnabled()
        composeTestRule
            .onNodeWithContentDescription("Sincronizar, no disponible")
            .assertIsDisplayed()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithContentDescription("Más opciones").performClick()
        listOf("Reportes", "Exportar", "Acerca de").forEach { label ->
            composeTestRule
                .onNodeWithContentDescription("$label, no disponible")
                .assertIsDisplayed()
                .assertIsNotEnabled()
        }
    }
}
