package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.EstadisticaPartidoDTO
import services.EstadisticasJugadorService

fun Application.estadisticasJugadorRoutes() {
    val estadisticasJugadorService = EstadisticasJugadorService()

    routing {
        authenticate("auth-jwt") {
            route("/estadisticas-jugador") {
                get("/{idJugador}/partidos/{idPartido}") {
                    val idJugador = call.parameters["idJugador"]?.toIntOrNull()
                    val idPartido = call.parameters["idPartido"]?.toIntOrNull()

                    if (idJugador == null || idPartido == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del jugador inválido")
                        return@get
                    }

                    val estadisticas = estadisticasJugadorService.getEstadisticasByJugadorByPartido(idJugador, idPartido)
                    call.respond(estadisticas.map { it.toDTO() })
                }

                post {
                    val request = call.receive<Map<String, String>>()
                    val idJugador = request["idJugador"]?.toIntOrNull() ?: return@post call.respond("Falta el ID del jugador")
                    val idPartido  = request["idPartido"]?.toIntOrNull() ?: return@post call.respond("Falta el ID del partido")
                    val minutosJugados = request["minutosJugados"]?.toIntOrNull() ?: return@post
                    val goles = request["goles"]?.toIntOrNull() ?: return@post
                    val asistencias = request["asistencias"]?.toIntOrNull() ?: return@post
                    val titular = request["titular"].toBoolean()
                    val tarjetasAmarillas = request["tarjetasAmarillas"]?.toIntOrNull() ?: return@post
                    val tarjetasRojas = request["tarjetasRojas"]?.toIntOrNull() ?: return@post
                    val partidoJugado = request["partidoJugado"].toBoolean()

                    val estadisticaJugador = estadisticasJugadorService.createEstadisticasJugador(idJugador, idPartido, minutosJugados, goles, asistencias, titular, tarjetasAmarillas, tarjetasRojas, partidoJugado)
                    call.respond(HttpStatusCode.Created, "Estadistica partido creada con ID: ${estadisticaJugador.id.value}")
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val request = call.receive<Map<String, String>>()

                    val idJugador = request["idJugador"]?.toIntOrNull() ?: error("El ID del jugador no puede ser nulo")
                    val idPartido = request["idPartido"]?.toIntOrNull() ?: error("El ID del partido no puede ser nulo")
                    val idTemporada = request["idTemporada"]?.toIntOrNull() ?: error("El ID de la temporada no puede ser nulo")
                    val minutosJugados = request["minutosJugados"]?.toIntOrNull() ?: error("Los minutos no pueden ser nulos")
                    val goles = request["goles"]?.toIntOrNull() ?: error("Los goles no pueden ser nulos")
                    val asistencias = request["asistencias"]?.toIntOrNull() ?: error("Las asistencias no pueden ser nulas")
                    val titular = request["titular"]?.toBoolean() ?: error("La titularidad no puede ser nula")
                    val tarjetasAmarillas = request["tarjetasAmarillas"]?.toIntOrNull() ?: error("Las tarjetas amarillas no pueden ser nulas")
                    val tarjetasRojas = request["tarjetasRojas"]?.toIntOrNull() ?: error("Las tarjetas rojas no pueden ser nulas")
                    val partidoJugado = request["partidoJugado"]?.toBoolean() ?: error("El partido jugado no puede ser nulo")

                    val estadisticaJugador = estadisticasJugadorService.updateEstadisticasJugador(id, idJugador, idPartido, idTemporada, minutosJugados, goles, asistencias, titular, tarjetasAmarillas, tarjetasRojas, partidoJugado)

                    if (estadisticaJugador){
                        call.respond(HttpStatusCode.OK, "Estadistica jugador actualizado")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Estadistica jugador no encontrado")
                    }
                }

                // ← NUEVO ENDPOINT: detalle de una estadística de jugador a lo largo de la temporada
                get("/{idJugador}/equipo/{idEquipo}/temporada/{idTemporada}/detalle/{nomEstadistica}") {
                    val idJugador   = call.parameters["idJugador"]  ?.toIntOrNull()
                    val idEquipo    = call.parameters["idEquipo"]   ?.toIntOrNull()
                    val idTemporada = call.parameters["idTemporada"]?.toIntOrNull()
                    val nomEstad    = call.parameters["nomEstadistica"]

                    if (idJugador == null || idEquipo == null || idTemporada == null || nomEstad.isNullOrBlank()) {
                        return@get call.respond(
                            HttpStatusCode.BadRequest,
                            "Parámetros inválidos: idJugador, idEquipo, idTemporada y nomEstadistica son obligatorios"
                        )
                    }

                    val historico: List<EstadisticaPartidoDTO> =
                        estadisticasJugadorService
                            .getDetalleEstadisticaJugador(idJugador, idEquipo, idTemporada, nomEstad)

                    call.respond(historico)
                }
            }
        }
    }
}