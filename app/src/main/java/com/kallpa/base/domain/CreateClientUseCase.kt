package com.kallpa.base.domain

import com.kallpa.base.data.ClientRepository
import java.util.UUID

class CreateClientUseCase(private val repository: ClientRepository) {
    suspend operator fun invoke(client: Client): Client {
        val created = client.copy(id = UUID.randomUUID().toString())
        repository.save(created)
        return created
    }
}
