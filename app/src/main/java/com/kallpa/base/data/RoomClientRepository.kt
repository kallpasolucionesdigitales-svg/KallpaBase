package com.kallpa.base.data

import com.kallpa.base.data.local.ClientDao
import com.kallpa.base.data.local.toDomain
import com.kallpa.base.data.local.toEntity
import com.kallpa.base.domain.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomClientRepository(private val dao: ClientDao) : ClientRepository {
    override suspend fun list(): List<Client> = withContext(Dispatchers.IO) {
        dao.list().map { it.toDomain() }
    }

    override suspend fun get(id: String): Client? = withContext(Dispatchers.IO) {
        dao.get(id)?.toDomain()
    }

    override suspend fun save(client: Client) = withContext(Dispatchers.IO) {
        dao.save(client.toEntity())
    }

    override suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        dao.delete(id)
    }
}
