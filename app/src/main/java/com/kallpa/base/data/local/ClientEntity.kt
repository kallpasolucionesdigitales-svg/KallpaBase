package com.kallpa.base.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kallpa.base.domain.Client

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val telefono: String?,
    val email: String?,
    val direccion: String?,
)

fun Client.toEntity(): ClientEntity = ClientEntity(
    id = id,
    nombre = nombre,
    telefono = telefono,
    email = email,
    direccion = direccion,
)

fun ClientEntity.toDomain(): Client = Client(
    id = id,
    nombre = nombre,
    telefono = telefono,
    email = email,
    direccion = direccion,
)
