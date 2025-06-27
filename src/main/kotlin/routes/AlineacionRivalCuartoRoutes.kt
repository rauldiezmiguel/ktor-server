package routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import model.*
import services.AlineacionRivalCuartoService

fun Application.alineacionRivalCuartoRoutes() {
    val service = AlineacionRivalCuartoService()

    routing {
        authenticate("auth-jwt") {
            route("/alineacion/rival") {

                get("/cuarto/{idCuarto}") {
                    val idCuarto = call.parameters["idCuarto"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID del cuarto inválido")

                    val alineacion = service.getAlineacionRivalByCuarto(idCuarto)
                    call.respond(alineacion.map { it.toDTO() })
                }

                post {
                    val request = call.receive<CrearAlineacionRivalRequest>()

                    val alineacion = service.createAlineacionRival(
                        request.idCuarto,
                        request.dorsalJugador,
                        request.posX,
                        request.posY
                    )

                    call.respond(HttpStatusCode.OK, alineacion.toDTO())
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    val eliminado = id?.let { service.deleteAlineacionRival(it) } ?: false
                    if (eliminado) {
                        call.respond(HttpStatusCode.OK, "Alineación rival eliminada correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Alineación no encontrada")
                    }
                }
            }
        }
    }
}
