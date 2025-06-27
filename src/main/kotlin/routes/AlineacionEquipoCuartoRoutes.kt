package routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import model.*
import services.AlineacionEquipoCuartoService

fun Application.alineacionEquipoCuartoRoutes() {
    val service = AlineacionEquipoCuartoService()

    routing {
        authenticate("auth-jwt") {
            route("/alineacion/equipo") {

                get("/cuarto/{idCuarto}") {
                    val idCuarto = call.parameters["idCuarto"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID del cuarto inválido")

                    val alineacion = service.getAlineacionByCuarto(idCuarto)
                    call.respond(alineacion.map { it.toDTO() })
                }

                post {
                    val request = call.receive<CrearAlineacionEquipoRequest>()

                    val alineacion = service.createAlineacionJugador(
                        request.idCuarto,
                        request.idJugador,
                        request.posX,
                        request.posY
                    )

                    call.respond(HttpStatusCode.OK, alineacion.toDTO())
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