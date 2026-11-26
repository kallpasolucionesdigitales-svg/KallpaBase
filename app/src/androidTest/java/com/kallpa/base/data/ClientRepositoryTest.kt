package com.kallpa.base.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.kallpa.base.data.local.KallpaDatabase
import com.kallpa.base.domain.Client
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ClientRepositoryTest {
    private lateinit var database: KallpaDatabase
    private lateinit var repository: ClientRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, KallpaDatabase::class.java).build()
        repository = RoomClientRepository(database.clientDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun bajaEliminaRegistro() = runBlocking {
        val client = Client(id = "cliente-1", nombre = "Ana Pérez", email = "ana@example.com")
        repository.save(client)

        repository.delete(client.id)

        assertNull(repository.get(client.id))
        assertTrue(repository.list().isEmpty())
    }

    @Test
    fun altaGuardaConId() = runBlocking {
        val client = Client(
            id = "cliente-2",
            nombre = "Ana Pérez",
            telefono = "+51 999 000 111",
            email = "ana@example.com",
            direccion = "Lima",
        )

        repository.save(client)

        assertEquals(client, repository.get(client.id))
    }
}
