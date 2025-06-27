package routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import model.*
import services.CuartosPartidoService

fun Application.cuartosPartidoRoutes() {
    val cuartosService = CuartosPartidoService()

    routing {
        authenticate("auth-jwt") {
            route("/cuartos") {

                get("/partido/{idPartido}") {
                    val idPartido = call.parameters["idPartido"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID del partido inválido")

                    val cuartos = cuartosService.getCuartosByPartido(idPartido)
                    call.respond(cuartos.map { it.toDTO() })
                }

                post {
                    val request = call.receive<CrearCuartosPartidoRequest>()
                    val cuarto = cuartosService.createCuarto(request.idPartido, request.numero)
                    call.respond(cuarto.toDTO())
                }

                put("/equipo/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val request = call.receive<ModificarCuartosEquipoRequest>()

                    val actualizado = cuartosService.updateCuartoEquipo(
                        id,
                        request.funcionamiento,
                        request.danoRival,
                        request.observaciones
                    )

                    if (actualizado != null) {
                        call.respond(HttpStatusCode.OK, actualizado.toDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Cuarto no encontrado")
                    }
                }

                put("/rival/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val request = call.receive<ModificarCuartosRivalRequest>()

                    val actualizado = cuartosService.updateCuartoRival(
                        id,
                        request.analisisRival,
                        request.observacionesRival
                    )

                    if (actualizado != null) {
                        call.respond(HttpStatusCode.OK, actualizado.toDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Cuarto no encontrado")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    val eliminado = id?.let { cuartosService.deleteCuarto(it) } ?: false
                    if (eliminado) {
                        call.respond(HttpStatusCode.OK, "Cuarto eliminado correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Cuarto no encontrado")
                    }
                }
            }
        }
    }
}
