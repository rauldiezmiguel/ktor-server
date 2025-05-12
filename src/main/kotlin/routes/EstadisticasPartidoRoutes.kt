package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.EstadisticasPartidoService

fun Application.estadisticasPartidoRoutes() {
    val estadisticasPartidoService = EstadisticasPartidoService()

    routing {
        authenticate("auth-jwt") {
            route("/estadisticas-partidos") {
                get("/{idPartido}") {
                    val idPartido = call.parameters["idPartido"]?.toIntOrNull()
                    if (idPartido == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del partido inválido")
                        return@get
                    }

                    val estadisticas = estadisticasPartidoService.getEstadisticasPartidoById(idPartido)
                    call.respond(estadisticas.map { it.toDTO() })
                }

                get("/{idEquipo}/temporada/{idTemporada}/detalle/{nomEstadistica}"){
                    val idEquipo = call.parameters["idEquipo"]?.toIntOrNull()
                    val idTemporada = call.parameters["idTemporada"]?.toIntOrNull()
                    val nomEstadistica = call.parameters["nomEstadistica"]?.toString()

                    val lista = estadisticasPartidoService.getDetalleEstadisticaEquipo(idEquipo, idTemporada, nomEstadistica)

                    call.respond(lista)
                }
            }
        }
    }
}