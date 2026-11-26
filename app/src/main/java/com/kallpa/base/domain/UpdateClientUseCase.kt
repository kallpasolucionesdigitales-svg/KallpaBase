package com.kallpa.base.domain

import com.kallpa.base.data.ClientRepository

class UpdateClientUseCase(private val repository: ClientRepository) {
    suspend operator fun invoke(client: Client) {
        repository.save(client)
    }
}
