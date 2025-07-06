package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.PartidoService
import model.ActualizarPartidoRequest
import model.CrearPartidoRequest

fun Application.partidoRoutes() {
    val partidoService = PartidoService()

    routing {
        authenticate("auth-jwt") {
            route("/partidos") {
                get("/equipos/{idEquipo}") {
                    val idEquipo = call.parameters["idEquipo"]?.toIntOrNull()

                    if (idEquipo == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del equipo inválido")
                        return@get
                    }

                    val partidos = partidoService.getPartidosByEquipo(idEquipo)
                    call.respond(partidos.map { it.toDTO() })
                }

                post {
                    val request = call.receive<CrearPartidoRequest>()

                    val partido = partidoService.crearPartidoConCuartos(
                        idEquipo = request.idEquipo,
                        nombreRival = request.nombreRival,
                        fecha = request.fecha
                    )

                    call.respond(HttpStatusCode.OK, partido.toDTO())
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val request = call.receive<ActualizarPartidoRequest>()

                    val partidoUpdate = partidoService.updatePartido(
                        id = id,
                        resultadoNumerico = request.resultadoNumerico,
                        resultado = request.resultado,
                        jugadoresDestacados = request.jugadoresDestacados
                    )

                    if (partidoUpdate != null) {
                        call.respond(HttpStatusCode.OK, partidoUpdate.toDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Partido no encontrado")
                    }
                }

                delete("/{id}"){
                    val id = call.parameters["id"]?.toIntOrNull()

                    val partidoDeleted = id?.let { partidoService.deletePartido(it) } ?: false
                    if (partidoDeleted) {
                        call.respond(HttpStatusCode.OK, "Partido eliminado correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Partido no encontrado")
                    }
                }
            }
        }
    }
}