package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.TemporadaService

fun Application.temporadasRoutes() {
    val temporadaService = TemporadaService()

    routing {
        authenticate("auth-jwt") {
            route("/temporadas"){
                get {
                    val temporadas = temporadaService.getAllTemporadas()
                    call.respond(temporadas.map { it.toDTO() })
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val temporada = id?.let { temporadaService.getTemporadaById(it) }
                    if (temporada != null) {
                        call.respond(temporada.toDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                    }
                }
            }
        }
    }
}