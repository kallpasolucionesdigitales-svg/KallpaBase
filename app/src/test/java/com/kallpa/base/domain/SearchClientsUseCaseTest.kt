package com.kallpa.base.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchClientsUseCaseTest {
    private val search = SearchClientsUseCase()

    @Test
    fun filtraPorNombre() {
        val ana = Client(id = "1", nombre = "Ana Pérez")
        val luis = Client(id = "2", nombre = "Luis Gómez")
        val marta = Client(id = "3", nombre = "Marta Ruiz")

        val result = search(listOf(ana, luis, marta), "ana")

        assertEquals(listOf(ana), result)
    }

    @Test
    fun filtraPorEmailIgnorandoMayusculas() {
        val ana = Client(id = "1", nombre = "Ana Pérez", email = "ana.perez@correo.com")
        val luis = Client(id = "2", nombre = "Luis Gómez", email = "otro@ejemplo.com")

        val result = search(listOf(ana, luis), "CORREO.COM")

        assertEquals(listOf(ana), result)
    }

    @Test
    fun filtraPorTelefono() {
        val ana = Client(id = "1", nombre = "Ana Pérez", telefono = "+51 999 000 111")
        val luis = Client(id = "2", nombre = "Luis Gómez", telefono = "+51 987 654 321")

        val result = search(listOf(ana, luis), "999 000")

        assertEquals(listOf(ana), result)
    }

    @Test
    fun filtraPorDireccion() {
        val ana = Client(id = "1", nombre = "Ana Pérez", direccion = "Av. Lima 123")
        val luis = Client(id = "2", nombre = "Luis Gómez", direccion = "Av. Piura 45")

        val result = search(listOf(ana, luis), "lima")

        assertEquals(listOf(ana), result)
    }

    @Test
    fun consultaVacia_devuelveTodosEnOrden() {
        val clients = listOf(
            Client(id = "1", nombre = "Ana Pérez"),
            Client(id = "2", nombre = "Luis Gómez"),
            Client(id = "3", nombre = "Marta Ruiz"),
        )

        val result = search(clients, "   ")

        assertEquals(clients, result)
    }

    @Test
    fun camposNulosOEnBlanco_noGeneranCoincidencias() {
        val client = Client(
            id = "1",
            nombre = "Ana Pérez",
            telefono = null,
            email = "",
            direccion = "   ",
        )

        val result = search(listOf(client), "sin-dato")

        assertEquals(emptyList<Client>(), result)
    }
}
