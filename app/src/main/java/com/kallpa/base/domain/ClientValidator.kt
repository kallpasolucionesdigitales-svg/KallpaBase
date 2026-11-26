package com.kallpa.base.domain

data class ClientValidationErrors(val nombre: String? = null, val email: String? = null) {
    val isValid: Boolean
        get() = nombre == null && email == null
}

object ClientValidator {
    fun validate(client: Client): ClientValidationErrors {
        val nombreError = if (client.nombre.isBlank()) "El nombre es obligatorio" else null
        val emailError = if (!client.email.isNullOrBlank() && !client.email.contains("@")) {
            "El email no es válido"
        } else {
            null
        }
        return ClientValidationErrors(nombre = nombreError, email = emailError)
    }
}
