package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.AsistenciaEntrenamientoDTO
import services.AsistenciaEntrenamientosService

fun Application.asistenciaEntrenosRoutes() {
    val asistenciaEntrenamientosService = AsistenciaEntrenamientosService()

    routing {
        authenticate("auth-jwt") {
            route("/asistencia-entrenamientos") {
                // Obtener lista de asistencias
                get("/{idEntrenamiento}") {
                    val idEnt = call.parameters["idEntrenamiento"]!!.toInt()
                    val dto  = asistenciaEntrenamientosService.getAsistencias(idEnt)
                    call.respond(dto)
                }

                // Guardar lista de asistencias en bloque
                post("/{idEntrenamiento}") {
                    val idEnt = call.parameters["idEntrenamiento"]!!.toInt()
                    val body: List<AsistenciaEntrenamientoDTO> = call.receive()
                    body.forEach {
                        asistenciaEntrenamientosService.marcarAsistencia(idEnt, it.idJugador, it.asistio, it.motivoInasistencia)
                    }
                    call.respond(HttpStatusCode.OK)
                }

                get("/jugador/{idJugador}") {
                    val idJugador = call.parameters["idJugador"]?.toIntOrNull()
                    if (idJugador == null) {
                        return@get call.respond(HttpStatusCode.BadRequest, "Id jugador inválido")
                    }
                    val lista = asistenciaEntrenamientosService.getAsistenciasPorJugador(idJugador)
                    call.respond(lista)
                }
            }
        }
    }
}