package com.kallpa.base.domain

import com.kallpa.base.data.ClientRepository

class ListClientsUseCase(private val repository: ClientRepository) {
    suspend operator fun invoke(): List<Client> = repository.list()
}
