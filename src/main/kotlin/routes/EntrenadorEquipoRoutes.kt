package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.EntrenadorEquipoService

fun Application.entrenadorEquipoRoutes() {
    val entrenadorEquipoService = EntrenadorEquipoService()

    routing {
        authenticate("auth-jwt") {
            route("/entrenadores") {
                get {
                    val relaciones = entrenadorEquipoService.getAllEntrenadoresEquipo()
                    call.respond(relaciones.map { it.toDTO() })
                }

                get("/equipos/{idClub}"){
                    val idClub = call.parameters["idClub"]?.toInt()
                    if (idClub == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del club inválido")
                        return@get
                    }

                    val equipos = entrenadorEquipoService.getEquiposByClub(idClub)
                    call.respond(equipos.map { it.toDTO() })
                }

                get("/entrenador/{idEntrenador}") {
                    val idEntrenador = call.parameters["idEntrenador"]?.toInt()
                    if (idEntrenador == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID de entrenador inválido")
                        return@get
                    }

                    val equipos = entrenadorEquipoService.getEquiposByEntrenador(idEntrenador)
                    call.respond(equipos.map { it.toDTO() })
                }

                get("/equipo/{idEquipo}") {
                    val idEquipo = call.parameters["idEquipo"]?.toInt()
                    if (idEquipo == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID de equipo inválido")
                        return@get
                    }

                    val entrenadores = entrenadorEquipoService.getEntrenadoresByEquipo(idEquipo)

                    println("DEBUG entrenadores JSON: " + entrenadores.map { it.toDTO() })

                    call.respond(entrenadores.map { it.toDTO() })
                }

                post {
                    val request = call.receive<Map<String, String>>()
                    val idEntrenador = request["idEntrenador"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta el ID del entrenador")
                    val idEquipo = request["idEquipo"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta el ID del equipo")
                    val idTemporada = request["idTemporada"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta el ID de la temporada")

                    val entrenadorEquipo = entrenadorEquipoService.addEntrenadorToEquipo(idEntrenador, idEquipo, idTemporada)
                    call.respond("Usuario asignado a equipo con ID: ${entrenadorEquipo.id.value}")
                }

                delete {
                    val request = call.receive<Map<String, String>>()
                    val idEntrenador = request["idEntrenador"]?.toIntOrNull() ?: return@delete call.respondText("Falta el ID del entrenador")
                    val idEquipo = request["idEquipo"]?.toIntOrNull() ?: return@delete call.respondText("Falta el ID del equipo")

                    val eliminado = entrenadorEquipoService.deleteEntrenadorDeEquipo(idEntrenador, idEquipo)

                    if (eliminado) {
                        call.respond(HttpStatusCode.OK, "Relación eliminada correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "No se encontró la relación")
                    }
                }
            }
        }
    }
}