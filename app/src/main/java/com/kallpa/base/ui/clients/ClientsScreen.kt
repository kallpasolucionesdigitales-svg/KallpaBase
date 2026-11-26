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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kallpa.base.data.ClientRepository
import com.kallpa.base.domain.Client

@Composable
fun ClientsScreen(repository: ClientRepository, modifier: Modifier = Modifier) {
    val state = rememberClientsState(repository)
    ClientsContent(
        uiState = state.uiState,
        onNewClick = state::onNewClick,
        onEditClick = state::onEditClick,
        onDeleteClick = state::onDeleteClick,
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
    onNewClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
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
            if (uiState.clients.isEmpty() && !uiState.isLoading) {
                Text("Sin clientes")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items = uiState.clients, key = { it.id }) { client ->
                        ClientRow(
                            client = client,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick,
                        )
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
}

@Composable
private fun ClientRow(
    client: Client,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
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
