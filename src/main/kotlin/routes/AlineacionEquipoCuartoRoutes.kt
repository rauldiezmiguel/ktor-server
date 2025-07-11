package routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.response.respond
import model.*
import services.AlineacionEquipoCuartoService

fun Application.alineacionEquipoCuartoRoutes() {
    val service = AlineacionEquipoCuartoService()

    routing {
        authenticate("auth-jwt") {
            route("/alineacion-equipo") {

                get("/cuarto/{idCuarto}") {
                    val idCuarto = call.parameters["idCuarto"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID del cuarto inválido")

                    val alineacion = service.getAlineacionByCuarto(idCuarto)
                    call.respond(alineacion.map { it.toDTO() })
                }

                post {
                    val request = call.receive<CrearAlineacionEquipoRequest>()

                    val alineacion = service.createAlineacionEquipoCuarto(
                        request.idCuarto
                    )

                    call.respond(HttpStatusCode.OK, alineacion.toDTO())
                }

                put("/{id}}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val request = call.receive<AddPlayerAlineacionEquipoRequest>()

                    val alineacionCuartoUpdate = service.actualizarPlayerAlineacionJugador(
                        id = id,
                        posX = request.posX,
                        posY = request.posY
                    )

                    if (alineacionCuartoUpdate != null) {
                        call.respond(HttpStatusCode.OK, alineacionCuartoUpdate.toDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Partido no encontrado")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    val eliminado = id?.let { service.deleteAlineacionJugador(it) } ?: false
                    if (eliminado) {
                        call.respond(HttpStatusCode.OK, "Alineación eliminada correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Alineación no encontrada")
                    }
                }
            }
        }
    }
}