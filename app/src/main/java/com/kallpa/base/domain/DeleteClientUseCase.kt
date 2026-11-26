package com.kallpa.base.domain

import com.kallpa.base.data.ClientRepository

class DeleteClientUseCase(private val repository: ClientRepository) {
    suspend operator fun invoke(id: String) {
        repository.delete(id)
    }
}
