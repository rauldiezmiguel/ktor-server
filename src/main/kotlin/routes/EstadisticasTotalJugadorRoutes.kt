package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import services.EstadisticasTotalJugadorService

fun Application.estadisticasTotalJugadorRoutes() {
    val estadisticasTotalJugadorService = EstadisticasTotalJugadorService()

    routing {
        authenticate("auth-jwt") {
            route("/estadisticas-total-jugador") {
                get("/{idJugador}/temporada/{idTemporada}") {
                    val idJugador = call.parameters["idJugador"]?.toIntOrNull()
                    val idTemporada = call.parameters["idTemporada"]?.toIntOrNull()
                    if (idJugador == null || idTemporada == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del jugador inválido")
                        return@get
                    }

                    val estadisticas = estadisticasTotalJugadorService.getEstadisticasJugadorByTemporada(idJugador, idTemporada)
                    call.respond(estadisticas.map { it.toDTO() })
                }
            }
        }
    }
}