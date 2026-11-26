package com.kallpa.base.domain

data class Client(
    val id: String,
    val nombre: String,
    val telefono: String? = null,
    val email: String? = null,
    val direccion: String? = null,
)
