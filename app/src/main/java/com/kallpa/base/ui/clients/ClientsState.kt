package com.kallpa.base.ui.clients

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kallpa.base.data.ClientRepository
import com.kallpa.base.domain.Client
import com.kallpa.base.domain.ClientValidator
import com.kallpa.base.domain.CreateClientUseCase
import com.kallpa.base.domain.DeleteClientUseCase
import com.kallpa.base.domain.ListClientsUseCase
import com.kallpa.base.domain.UpdateClientUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
@Suppress("TooManyFunctions") // State holder: un evento público por acción de la UI (spec 004).
class ClientsState internal constructor(
    private val repository: ClientRepository,
    private val scope: CoroutineScope,
) {
    var uiState by mutableStateOf(ClientsUiState())
        private set

    private val listClients = ListClientsUseCase(repository)
    private val createClient = CreateClientUseCase(repository)
    private val updateClient = UpdateClientUseCase(repository)
    private val deleteClient = DeleteClientUseCase(repository)

    fun load() {
        scope.launch {
            uiState = uiState.copy(clients = listClients(), isLoading = false)
        }
    }

    fun onNewClick() {
        uiState = uiState.copy(form = ClientFormState(), editingId = null)
    }

    fun onEditClick(id: String) {
        val client = uiState.clients.firstOrNull { it.id == id } ?: return
        uiState = uiState.copy(
            form = ClientFormState(
                nombre = client.nombre,
                telefono = client.telefono.orEmpty(),
                email = client.email.orEmpty(),
                direccion = client.direccion.orEmpty(),
            ),
            editingId = client.id,
        )
    }

    fun onDeleteClick(id: String) {
        uiState = uiState.copy(pendingDeleteId = id)
    }

    fun onFieldChange(field: ClientField, value: String) {
        val form = uiState.form ?: return
        uiState = uiState.copy(
            form = when (field) {
                ClientField.NOMBRE -> form.copy(nombre = value, nombreError = null)
                ClientField.TELEFONO -> form.copy(telefono = value)
                ClientField.EMAIL -> form.copy(email = value, emailError = null)
                ClientField.DIRECCION -> form.copy(direccion = value)
            },
        )
    }

    fun onSaveClick() {
        val form = uiState.form ?: return
        val isEditing = uiState.editingId != null
        val client = Client(
            id = uiState.editingId.orEmpty(),
            nombre = form.nombre.trim(),
            telefono = form.telefono.trim().ifBlank { null },
            email = form.email.trim().ifBlank { null },
            direccion = form.direccion.trim().ifBlank { null },
        )
        val errors = ClientValidator.validate(client)
        if (!errors.isValid) {
            uiState = uiState.copy(
                form = form.copy(nombreError = errors.nombre, emailError = errors.email),
            )
            return
        }
        scope.launch {
            if (isEditing) {
                updateClient(client)
            } else {
                createClient(client)
            }
            uiState = uiState.copy(form = null, editingId = null)
            load()
        }
    }

    fun onCancelForm() {
        uiState = uiState.copy(form = null, editingId = null)
    }

    fun onConfirmDelete() {
        val id = uiState.pendingDeleteId ?: return
        scope.launch {
            deleteClient(id)
            uiState = uiState.copy(pendingDeleteId = null)
            load()
        }
    }

    fun onCancelDelete() {
        uiState = uiState.copy(pendingDeleteId = null)
    }

    fun onViewClick(id: String) {
        uiState = uiState.copy(detailClientId = id)
    }

    fun onCloseDetail() {
        uiState = uiState.copy(detailClientId = null)
    }

    fun onEditFromDetail() {
        val id = uiState.detailClientId ?: return
        uiState = uiState.copy(detailClientId = null)
        onEditClick(id)
    }
}
