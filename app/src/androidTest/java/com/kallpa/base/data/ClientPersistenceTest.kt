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

class ClientPersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val databaseName = "kallpa-persistence-test.db"
    private lateinit var database: KallpaDatabase

    @Before
    fun setUp() {
        context.deleteDatabase(databaseName)
    }

    @After
    fun tearDown() {
        if (::database.isInitialized) {
            database.close()
        }
        context.deleteDatabase(databaseName)
    }

    private fun reopen(): ClientRepository {
        if (::database.isInitialized) {
            database.close()
        }
        database = Room.databaseBuilder(context, KallpaDatabase::class.java, databaseName).build()
        return RoomClientRepository(database.clientDao())
    }

    @Test
    fun altaSobreviveReinicio() = runBlocking {
        val client = Client(
            id = "cliente-1",
            nombre = "Ana Pérez",
            telefono = "+51 999 000 111",
            email = "ana@example.com",
            direccion = "Lima",
        )
        reopen().save(client)

        val restored = reopen().list().single()

        assertEquals(client, restored)
    }

    @Test
    fun bajaSobreviveReinicio() = runBlocking {
        val client = Client(id = "cliente-2", nombre = "Ana Pérez")
        val repository = reopen()
        repository.save(client)
        repository.delete(client.id)

        val afterRestart = reopen()

        assertNull(afterRestart.get(client.id))
        assertTrue(afterRestart.list().isEmpty())
    }
}
