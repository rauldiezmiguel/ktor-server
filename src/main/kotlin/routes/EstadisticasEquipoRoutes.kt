package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.EstadisticasEquipoService

fun Application.estadisticasEquipoRoutes() {
    val estadisticasEquipoService = EstadisticasEquipoService()

    routing {
        authenticate("auth-jwt") {
            route("/estadisticas-equipo") {
                get("/{idEquipo}/temporada/{idTemporada}") {
                    val idEquipo = call.parameters["idEquipo"]?.toIntOrNull()
                    val idTemporada = call.parameters["idTemporada"]?.toIntOrNull()
                    if (idEquipo == null || idTemporada == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID del equipo inválido")
                        return@get
                    }

                    val estadisticas = estadisticasEquipoService.getEstadisticasEquipoByTemporada(idEquipo, idTemporada)
                    call.respond(HttpStatusCode.OK, estadisticas.map { it.toDTO() })
                }

                get("/{idEquipo}/temporada/{idTemporada}/detalle-agregado/{nomEstadistica}") {
                    val idEquipoParam    = call.parameters["idEquipo"]?.toIntOrNull()
                    val idTemporadaParam = call.parameters["idTemporada"]?.toIntOrNull()
                    val estadisticaName  = call.parameters["nomEstadistica"]?.lowercase()

                    if (idEquipoParam == null || idTemporadaParam == null || estadisticaName.isNullOrBlank()) {
                        call.respond(HttpStatusCode.BadRequest, "Parámetros inválidos")
                        return@get
                    }

                    // Invocar el servicio
                    val resultado = estadisticasEquipoService.getEstadisticaEquipoConTotales(
                        idEquipo       = idEquipoParam,
                        idTemporada    = idTemporadaParam,
                        nomEstadistica = estadisticaName
                    )

                    call.respond(HttpStatusCode.OK, resultado)
                }
            }
        }
    }
}