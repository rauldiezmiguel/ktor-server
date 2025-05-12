package com.example

import application.DatabaseFactory
import application.module
import authentication.PasswordHasher
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.h2.engine.User
import kotlin.test.*

class ApplicationTest {
    @BeforeTest
    fun setup() {
        //Inicializamos la base de datos antes de correr los test
        DatabaseFactory.init()
    }


    /*
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            println("Código de respuesta: ${status}")
            println("Cuerpo de respuesta: ${bodyAsText()}")

            assertEquals(HttpStatusCode.OK, status)
            assertEquals("API funcionando correctamente", bodyAsText()) // Verificamos la respuesta
        }
    }

    @Test
    fun testHashPassword() = testApplication {
        val contraseña = "1234"
        val hashedContraseña = PasswordHasher.hashPassword(contraseña)

        println("Contraseña original: $contraseña")
        println("Contraseña hasheada: $hashedContraseña")

        // Verificamos que el hash no sea igual que la contraseña original
        assertNotEquals(contraseña, hashedContraseña)
    }

    @Test
    fun testVerifyPassword() = testApplication {
        val contraseña = "1234"
        val hashedContraseña = PasswordHasher.hashPassword(contraseña)

        println("Contraseña original: $contraseña")
        println("Contraseña hasheada: $hashedContraseña")

        // Verificamos que la verificación funcione
        assertTrue(PasswordHasher.verifyPassword(contraseña, hashedContraseña))

        println(PasswordHasher.verifyPassword(contraseña, hashedContraseña))
    }

     */
}
