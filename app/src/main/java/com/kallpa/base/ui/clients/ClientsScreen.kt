package com.kallpa.base.ui.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kallpa.base.data.ClientRepository
import com.kallpa.base.domain.Client
import com.kallpa.base.domain.SearchClientsUseCase

@Composable
fun ClientsScreen(repository: ClientRepository, modifier: Modifier = Modifier) {
    ClientsScreen(repository = repository, modifier = modifier, searchRequest = 0)
}

@Composable
internal fun ClientsScreen(
    repository: ClientRepository,
    modifier: Modifier = Modifier,
    searchRequest: Int,
) {
    val state = rememberClientsState(repository)
    var query by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val searchClients = remember { SearchClientsUseCase() }
    val visibleClients = remember(state.uiState.clients, query) {
        searchClients(state.uiState.clients, query)
    }

    LaunchedEffect(searchRequest) {
        if (searchRequest > 0) {
            focusRequester.requestFocus()
        }
    }

    ClientsContent(
        uiState = state.uiState,
        visibleClients = visibleClients,
        query = query,
        onQueryChange = { query = it },
        onClearQuery = { query = "" },
        searchFieldModifier = Modifier.focusRequester(focusRequester),
        onNewClick = state::onNewClick,
        onEditClick = state::onEditClick,
        onDeleteClick = state::onDeleteClick,
        onViewClick = state::onViewClick,
        onCloseDetail = state::onCloseDetail,
        onEditFromDetail = state::onEditFromDetail,
        onFieldChange = state::onFieldChange,
        onSaveClick = state::onSaveClick,
        onCancelForm = state::onCancelForm,
        onConfirmDelete = state::onConfirmDelete,
        onCancelDelete = state::onCancelDelete,
        modifier = modifier,
    )
}

@Composable
private fun ClientsContent(
    uiState: ClientsUiState,
    visibleClients: List<Client>,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    searchFieldModifier: Modifier,
    onNewClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onViewClick: (String) -> Unit,
    onCloseDetail: () -> Unit,
    onEditFromDetail: () -> Unit,
    onFieldChange: (ClientField, String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelForm: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Button(onClick = onNewClick) {
                Text("Nuevo cliente")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Clientes",
                style = MaterialTheme.typography.headlineMedium,
            )
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Buscar cliente") },
                singleLine = true,
                modifier = searchFieldModifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Buscar cliente" },
            )
            if (query.isNotBlank()) {
                Button(
                    onClick = onClearQuery,
                    modifier = Modifier.semantics { contentDescription = "Limpiar búsqueda" },
                ) {
                    Text("Limpiar")
                }
            }
            when {
                uiState.clients.isEmpty() && query.isBlank() && !uiState.isLoading -> {
                    Text("Sin clientes")
                }
                visibleClients.isEmpty() && !uiState.isLoading -> {
                    Text("Sin resultados")
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items = visibleClients, key = { it.id }) { client ->
                            ClientRow(
                                client = client,
                                onEditClick = onEditClick,
                                onDeleteClick = onDeleteClick,
                                onViewClick = onViewClick,
                            )
                        }
                    }
                }
            }
        }
    }

    uiState.form?.let { form ->
        ClientFormDialog(
            form = form,
            isEditing = uiState.editingId != null,
            onFieldChange = onFieldChange,
            onSaveClick = onSaveClick,
            onCancelClick = onCancelForm,
        )
    }

    if (uiState.pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = onCancelDelete,
            title = { Text("Eliminar cliente") },
            text = { Text("¿Confirmar la baja de este cliente?") },
            confirmButton = {
                Button(onClick = onConfirmDelete) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = onCancelDelete) {
                    Text("Cancelar")
                }
            },
        )
    }

    uiState.detailClientId?.let { id ->
        uiState.clients.firstOrNull { it.id == id }?.let { client ->
            ClientDetailDialog(
                client = client,
                onCloseClick = onCloseDetail,
                onEditClick = onEditFromDetail,
            )
        }
    }
}

@Composable
private fun ClientRow(
    client: Client,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onViewClick: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = client.nombre, style = MaterialTheme.typography.titleMedium)
            client.email?.takeIf { it.isNotBlank() }?.let { Text(it) }
            client.telefono?.takeIf { it.isNotBlank() }?.let { Text(it) }
            client.direccion?.takeIf { it.isNotBlank() }?.let { Text(it) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onViewClick(client.id) }) {
                    Text("Ver ficha")
                }
                Button(onClick = { onEditClick(client.id) }) {
                    Text("Editar")
                }
                Button(onClick = { onDeleteClick(client.id) }) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
private fun ClientDetailDialog(client: Client, onCloseClick: () -> Unit, onEditClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCloseClick,
        title = { Text("Ficha de cliente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(client.nombre)
                client.email?.takeIf { it.isNotBlank() }?.let { Text(it) }
                client.telefono?.takeIf { it.isNotBlank() }?.let { Text(it) }
                client.direccion?.takeIf { it.isNotBlank() }?.let { Text(it) }
            }
        },
        confirmButton = {
            Button(
                onClick = onEditClick,
                modifier = Modifier.semantics { contentDescription = "Editar cliente" },
            ) {
                Text("Editar")
            }
        },
        dismissButton = {
            Button(
                onClick = onCloseClick,
                modifier = Modifier.semantics { contentDescription = "Cerrar ficha" },
            ) {
                Text("Cerrar")
            }
        },
    )
}

@Composable
private fun ClientFormDialog(
    form: ClientFormState,
    isEditing: Boolean,
    onFieldChange: (ClientField, String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelClick,
        title = { Text(if (isEditing) "Editar cliente" else "Nuevo cliente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = form.nombre,
                    onValueChange = { onFieldChange(ClientField.NOMBRE, it) },
                    label = { Text("Nombre") },
                    isError = form.nombreError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                form.nombreError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                OutlinedTextField(
                    value = form.telefono,
                    onValueChange = { onFieldChange(ClientField.TELEFONO, it) },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = form.email,
                    onValueChange = { onFieldChange(ClientField.EMAIL, it) },
                    label = { Text("Email") },
                    isError = form.emailError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                form.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                OutlinedTextField(
                    value = form.direccion,
                    onValueChange = { onFieldChange(ClientField.DIRECCION, it) },
                    label = { Text("Dirección") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(4.dp))
            }
        },
        confirmButton = {
            Button(onClick = onSaveClick) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onCancelClick) {
                Text("Cancelar")
            }
        },
    )
}
