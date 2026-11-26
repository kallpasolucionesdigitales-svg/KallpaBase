package com.kallpa.base.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kallpa.base.data.ClientRepository
import com.kallpa.base.ui.clients.ClientsScreen

private const val SEARCH_DESCRIPTION = "Buscar, no disponible"
private const val SYNC_DESCRIPTION = "Sincronizar, no disponible"
private const val OVERFLOW_DESCRIPTION = "Más opciones"

private val overflowActions = listOf("Reportes", "Exportar", "Acerca de")

enum class MainDestination(
    val title: String,
    val indicator: String?,
    val enabled: Boolean,
    val contentDescription: String,
) {
    CLIENTS(
        title = "Clientes",
        indicator = null,
        enabled = true,
        contentDescription = "Clientes, disponible",
    ),
    BUDGETS(
        title = "Presupuestos",
        indicator = "Próximamente",
        enabled = false,
        contentDescription = "Presupuestos, no disponible, Próximamente",
    ),
    OPERATIONS(
        title = "Operaciones",
        indicator = "Fase 2",
        enabled = false,
        contentDescription = "Operaciones, no disponible, Fase 2",
    ),
    SETTINGS(
        title = "Configuración",
        indicator = "Próximamente",
        enabled = false,
        contentDescription = "Configuración, no disponible, Próximamente",
    ),
}

@Composable
fun MainScreen(repository: ClientRepository, modifier: Modifier = Modifier) {
    var selectedDestination by rememberSaveable { mutableStateOf(MainDestination.CLIENTS) }

    Scaffold(
        modifier = modifier,
        topBar = { MainTopBar(title = selectedDestination.title) },
        bottomBar = {
            MainNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationClick = { selectedDestination = it },
            )
        },
    ) { paddingValues ->
        when (selectedDestination) {
            MainDestination.CLIENTS -> ClientsScreen(
                repository = repository,
                modifier = Modifier.padding(paddingValues),
            )
            MainDestination.BUDGETS,
            MainDestination.OPERATIONS,
            MainDestination.SETTINGS,
            -> Unit
        }
    }
}

@Composable
private fun MainTopBar(title: String) {
    var overflowExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.semantics { contentDescription = "Título, $title" },
            )
        },
        actions = {
            DisabledAction(label = "Buscar", description = SEARCH_DESCRIPTION)
            DisabledAction(label = "Sincronizar", description = SYNC_DESCRIPTION)
            TextButton(
                onClick = { overflowExpanded = true },
                modifier = Modifier.semantics { contentDescription = OVERFLOW_DESCRIPTION },
            ) {
                Text("Más opciones")
            }
            DropdownMenu(
                expanded = overflowExpanded,
                onDismissRequest = { overflowExpanded = false },
            ) {
                overflowActions.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.semantics {
                            contentDescription = "$label, no disponible"
                        },
                    )
                }
            }
        },
    )
}

@Composable
private fun DisabledAction(label: String, description: String) {
    TextButton(
        onClick = {},
        enabled = false,
        modifier = Modifier.semantics { contentDescription = description },
    ) {
        Text(label)
    }
}

@Composable
private fun MainNavigationBar(
    selectedDestination: MainDestination,
    onDestinationClick: (MainDestination) -> Unit,
) {
    NavigationBar {
        MainDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == selectedDestination,
                onClick = { if (destination.enabled) onDestinationClick(destination) },
                enabled = destination.enabled,
                icon = {
                    Text(
                        text = destination.title.first().toString(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                label = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(destination.title)
                        destination.indicator?.let { indicator ->
                            Text(indicator, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                alwaysShowLabel = true,
                modifier = Modifier.semantics(mergeDescendants = true) {
                    contentDescription = destination.contentDescription
                },
            )
        }
    }
}
