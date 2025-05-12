package com.example

import application.DatabaseFactory
import application.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRoutesTest {
    @BeforeTest
    fun setup() {
        transaction {

            DatabaseFactory.init()
        }
    }

    /*
    @Test
    fun testCreateUser() = testApplication {
        application {
            module()
        }

        val response = client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody("""{
                "nombreUsuario": "ra_entrenador",
                "contraseña": "rEntrenador",
                "tipoUsuario": "entrenador",
                "idClub": 1
            }""")
        }

        println("Código de respuesta: ${response.status}")
        println("Cuerpo de respuesta: ${response.bodyAsText()}")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(true, response.bodyAsText().contains("Usuario creado con ID"))
    }
     */

    /*
    @Test
    fun testLoginUser() = testApplication {
        application {
            module()
        }

        val response = client.post("/usuarios/login") {
            contentType(ContentType.Application.Json)
            setBody("""{
                "nombreUsuario": "ra_entrenador",",
                "contraseña": "rEntrenador"
            }""")
        }

        println("Código de respuesta: ${response.status}")
        println("Cuerpo de respuesta: ${response.bodyAsText()}")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Usuario o contraseña incorrectos", response.bodyAsText())
    }

     */
}