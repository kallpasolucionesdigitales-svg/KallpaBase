package com.kallpa.base.testing

import com.kallpa.base.data.ClientRepository
import com.kallpa.base.domain.Client

class FakeClientRepository : ClientRepository {
    private val clients = mutableMapOf<String, Client>()

    override suspend fun list(): List<Client> = clients.values.toList()

    override suspend fun get(id: String): Client? = clients[id]

    override suspend fun save(client: Client) {
        clients[client.id] = client
    }

    override suspend fun delete(id: String) {
        clients.remove(id)
    }
}
