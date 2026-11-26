package com.kallpa.base.data

import com.kallpa.base.domain.Client

interface ClientRepository {
    suspend fun list(): List<Client>
    suspend fun get(id: String): Client?
    suspend fun save(client: Client)
    suspend fun delete(id: String)
}
