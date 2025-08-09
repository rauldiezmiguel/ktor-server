package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import model.ActualizarJugadorDTO
import model.JugadorDTO
import services.ClubService
import services.JugadorService

fun Application.jugadorRoutes() {
    val jugadorService = JugadorService()

    routing {
        authenticate("auth-jwt") {
            route("/jugadores") {
                get("/{id}") {
                    val idJugador = call.parameters["id"]?.toIntOrNull()
                    if (idJugador == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID inválido")
                        return@get
                    }

                    val jugador = jugadorService.getJugadorById(idJugador)
                    if (jugador != null) {
                        call.respond(HttpStatusCode.OK, jugador.toDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Jugador no encontrado")
                    }
                }

                get("/equipos/{idEquipo}") {
                    val idEquipo = call.parameters["idEquipo"]?.toIntOrNull()
                    if (idEquipo == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID de equipo inválido")
                        return@get
                    }

                    val jugadores = jugadorService.getJugadorByEquipo(idEquipo).map { it.toDTO() }
                    call.respond(jugadores)
                }

                post {
                    val request = call.receive<Map<String, String>>()
                    val nombre = request["nombre"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta el nombre del jugador")
                    val dorsal = request["dorsal"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta el dorsal del jugador")
                    val posicion = request["posicion"]
                    val idEquipo = request["idEquipo"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta el ID del equipo")

                    val jugador = jugadorService.createJugador(nombre, dorsal, posicion, idEquipo)
                    call.respond(HttpStatusCode.OK, jugador.toDTO())
                }

                put("/{id}"){
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    val request = call.receive<ActualizarJugadorDTO>()

                    val dorsal = request.dorsal ?: return@put call.respond(HttpStatusCode.BadRequest, "Falta el dorsal del jugador")
                    val posicion = request.posicion ?: return@put call.respond(HttpStatusCode.BadRequest, "Falta la posicion del jugador")



                    val jugadorUpdate = jugadorService.updateJugador(id, dorsal, posicion)
                    if(jugadorUpdate) {
                        call.respond(HttpStatusCode.OK, "Jugador actualizado")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Jugador no encontrado")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    val jugadorDeleted = id?.let { jugadorService.deleteJugador(it) } ?: false
                    if (jugadorDeleted) {
                        call.respond(HttpStatusCode.OK, "Jugador eliminado correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Jugador no encontrado")
                    }
                }
            }
        }
    }
}