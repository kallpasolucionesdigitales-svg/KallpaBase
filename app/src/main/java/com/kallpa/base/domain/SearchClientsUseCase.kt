package com.kallpa.base.domain

import java.util.Locale

class SearchClientsUseCase {
    operator fun invoke(clients: List<Client>, query: String): List<Client> {
        val normalizedQuery = query.trim().lowercase(Locale.ROOT)
        if (normalizedQuery.isEmpty()) return clients

        return clients.filter { client ->
            listOf(client.nombre, client.email, client.telefono, client.direccion)
                .any { value ->
                    !value.isNullOrBlank() && value.lowercase(Locale.ROOT).contains(normalizedQuery)
                }
        }
    }
}
