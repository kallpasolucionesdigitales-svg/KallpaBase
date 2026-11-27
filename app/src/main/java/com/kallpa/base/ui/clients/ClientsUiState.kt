package com.kallpa.base.ui.clients

import com.kallpa.base.domain.Client

enum class ClientField {
    NOMBRE,
    TELEFONO,
    EMAIL,
    DIRECCION,
}

data class ClientFormState(
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "",
    val direccion: String = "",
    val nombreError: String? = null,
    val emailError: String? = null,
)

data class ClientsUiState(
    val clients: List<Client> = emptyList(),
    val form: ClientFormState? = null,
    val editingId: String? = null,
    val pendingDeleteId: String? = null,
    val detailClientId: String? = null,
    val isLoading: Boolean = true,
)
