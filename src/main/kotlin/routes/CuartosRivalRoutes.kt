package routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import model.*
import services.CuartosRivalService

fun Application.cuartosRivalRoutes() {
    val cuartosRivalService = CuartosRivalService()

    routing {
        authenticate("auth-jwt") {
            route("/cuartos-rival") {

                get("/partido/{idPartido}") {
                    val idPartido = call.parameters["idPartido"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID del partido inválido")

                    val cuartos = cuartosRivalService.getCuartosByPartido(idPartido)
                    call.respond(cuartos.map { it.toDTO() })
                }

                post {
                    val request = call.receive<CrearCuartoRivalRequest>()
                    val cuarto = cuartosRivalService.createCuarto(request.idPartido, request.numero)
                    call.respond(cuarto.toDTO())
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val request = call.receive<ModificarCuartosRivalRequest>()

                    val actualizado = cuartosRivalService.updateCuarto(
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

                    val eliminado = id?.let { cuartosRivalService.deleteCuarto(it) } ?: false
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
