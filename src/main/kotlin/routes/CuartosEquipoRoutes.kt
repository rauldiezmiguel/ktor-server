package routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import model.*
import services.CuartosEquipoService

fun Application.cuartosEquipoRoutes() {
    val cuartosEquipoService = CuartosEquipoService()

    routing {
        authenticate("auth-jwt") {
            route("/cuartos-equipo") {

                get("/partido/{idPartido}") {
                    val idPartido = call.parameters["idPartido"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID del partido inválido")

                    val cuartos = cuartosEquipoService.getCuartosByPartido(idPartido)

                    if (cuartos.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "No se encontraron cuartos para el partido $idPartido")
                    } else {
                        call.respond(cuartos.map { it.toDTO() })
                    }
                }

                post {
                    val request = call.receive<CrearCuartoEquipoRequest>()
                    val cuarto = cuartosEquipoService.createCuarto(request.idPartido, request.numero)
                    call.respond(cuarto.toDTO())
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val request = call.receive<ModificarCuartosEquipoRequest>()

                    val actualizado = cuartosEquipoService.updateCuarto(
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

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    val eliminado = id?.let { cuartosEquipoService.deleteCuarto(it) } ?: false
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
