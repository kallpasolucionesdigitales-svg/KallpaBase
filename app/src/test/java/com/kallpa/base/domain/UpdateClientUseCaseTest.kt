package com.kallpa.base.domain

import com.kallpa.base.testing.FakeClientRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateClientUseCaseTest {
    @Test
    fun mantieneId() = runBlocking {
        val repository = FakeClientRepository()
        val original = Client(id = "cliente-1", nombre = "Ana Pérez", telefono = "+51 999 000 111")
        repository.save(original)

        UpdateClientUseCase(repository)(original.copy(telefono = "+51 987 654 321"))

        val updated = repository.get("cliente-1")
        assertEquals("cliente-1", updated?.id)
        assertEquals("+51 987 654 321", updated?.telefono)
        assertEquals(1, repository.list().size)
    }
}
