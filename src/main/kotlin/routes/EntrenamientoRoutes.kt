package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.CrearEntrenamientoRequest
import services.EntrenamientoService
import services.PartidoService
import java.time.LocalDate

fun Application.entrenamientoRoutes() {
    val entrenamientoService = EntrenamientoService()

    routing {
        authenticate("auth-jwt") {
            route("/entrenamientos") {
                get("/equipos/{idEquipo}") {
                    val idEquipo = call.parameters["idEquipo"]?.toInt()

                    if (idEquipo == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del equipo inválido")
                        return@get
                    }

                    val entrenamientos = entrenamientoService.getEntrenamientosByEquipo(idEquipo)
                    call.respond(entrenamientos.map { it.toDTO() })
                }

                post {
                    val request = call.receive<CrearEntrenamientoRequest>()

                    val entrenamiento = entrenamientoService.createEntrenamiento(
                        fecha = request.fecha,
                        descripcion = request.descripcion,
                        entrenamientoUrl = request.entrenamientoUrl,
                        idEquipo = request.idEquipo
                    )

                    call.respond(HttpStatusCode.OK, entrenamiento.toDTO())
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID inválido")
                        return@delete
                    }

                    val deleted = entrenamientoService.deleteEntrenamientoById(id)

                    if (deleted) {
                        call.respond(HttpStatusCode.OK, "Entrenamiento eliminado correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Entrenamiento no encontrado")
                    }
                }
            }
        }
    }
}