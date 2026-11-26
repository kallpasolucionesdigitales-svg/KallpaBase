package com.kallpa.base.domain

import com.kallpa.base.testing.FakeClientRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateClientUseCaseTest {
    @Test
    fun guardaConId() = runBlocking {
        val repository = FakeClientRepository()
        val useCase = CreateClientUseCase(repository)

        val created = useCase(
            Client(
                id = "",
                nombre = "Ana Pérez",
                telefono = "+51 999 000 111",
                email = "ana@example.com",
            ),
        )

        assertTrue(created.id.isNotBlank())
        assertEquals(created, repository.get(created.id))
    }
}
