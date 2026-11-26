package com.kallpa.base.domain

import com.kallpa.base.testing.FakeClientRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteClientUseCaseTest {
    @Test
    fun bajaEliminaRegistro() = runBlocking {
        val repository = FakeClientRepository()
        val client = Client(id = "cliente-1", nombre = "Ana Pérez")
        repository.save(client)

        DeleteClientUseCase(repository)(client.id)

        assertNull(repository.get(client.id))
        assertTrue(repository.list().isEmpty())
    }
}
