package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.CrearEvaluacionRequest
import services.EvaluacionesService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Application.evaluacionesRoutes() {
    val evaluacionesService = EvaluacionesService()

    routing {
        authenticate("auth-jwt") {
            route("/evaluaciones") {
                get("/{idJugador}") {
                    val idJugador = call.parameters["idJugador"]?.toIntOrNull()
                    if (idJugador == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del jugador inválido")
                        return@get
                    }

                    val evaluaciones = evaluacionesService.getEvaluacionesByJugador(idJugador)
                    call.respond(evaluaciones.map { it.toDTO() })
                }

                post {
                    val req = call.receive<CrearEvaluacionRequest>()

                    val fecha = try {
                        LocalDate.parse(req.fecha)
                    } catch (e: Exception) {
                        return@post call.respond(HttpStatusCode.BadRequest, "Formato de fecha incorrecto. Usa YYYY-MM-DD")
                    }

                    val evaluacion = evaluacionesService.createEvaluaciones(
                        idJugador = req.idJugador,
                        fecha = fecha,
                        comportamiento = req.comportamiento,
                        tecnica = req.tecnica,
                        tactica = req.tactica,
                        observaciones = req.observaciones
                    )
                    call.respond(HttpStatusCode.Created, "Evaluación creada con ID: ${evaluacion.id.value}")
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    val request = call.receive<Map<String, String>>()

                    val idJugador = call.parameters["idJugador"]?.toIntOrNull() ?: return@put call.respond("Falta el ID del jugador")
                    val fechaString = request["fecha"] ?: return@put call.respond("Falta la fecha")
                    val comportamiento = request["comportamiento"]?.toIntOrNull() ?: return@put call.respond("Falta el comportamiento")
                    val tecnica = request["tecnica"]?.toIntOrNull() ?: return@put call.respond("Falta la tecnica")
                    val tactica = request["tactica"]?.toIntOrNull() ?: return@put call.respond("Falta la tactica")
                    val observaciones = request["observaciones"] ?: return@put

                    val fecha = try {
                        LocalDate.parse(fechaString)
                    } catch (e: Exception) {
                        return@put call.respond("Formato de fecha incorrecto. Usa YYYY-MM-DD")
                    }

                    val evaluacion = evaluacionesService.updateEvaluacion(id, idJugador, fecha, comportamiento, tecnica, tactica, observaciones)
                    if (evaluacion) {
                        call.respond(HttpStatusCode.OK, "Evaluación actualizada")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Evaluación no encontrada")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID  inválido")
                    val evaluacion = id.let { evaluacionesService.deleteEvaluacion(it) }

                    if (evaluacion) {
                        call.respond(HttpStatusCode.OK, "Evaluación eliminada")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Evaluación no encontrada")
                    }
                }

                get("/promedios/{idJugador}") {
                    val idJugador = call.parameters["idJugador"]?.toIntOrNull()
                    if (idJugador == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del jugador inválido")
                        return@get
                    }

                    val promedios = evaluacionesService.getPromediosMensuales(idJugador)
                    call.respond(promedios)
                }
            }
        }
    }
}