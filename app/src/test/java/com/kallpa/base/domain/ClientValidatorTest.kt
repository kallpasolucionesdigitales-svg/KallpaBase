package com.kallpa.base.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClientValidatorTest {
    @Test
    fun camposInvalidos() {
        val errors = ClientValidator.validate(
            Client(id = "1", nombre = "  ", email = "ana"),
        )

        assertEquals("El nombre es obligatorio", errors.nombre)
        assertEquals("El email no es válido", errors.email)
        assertFalse(errors.isValid)
    }

    @Test
    fun camposValidos_noDevuelveErrores() {
        val errors = ClientValidator.validate(
            Client(id = "1", nombre = "Ana Pérez", email = "ana@example.com"),
        )

        assertTrue(errors.isValid)
    }
}
