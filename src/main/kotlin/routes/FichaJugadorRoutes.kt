package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import model.FichaJugadorDTO
import model.FichaJugadorRequest
import services.FichaJugadorService

fun Application.fichaJugadorRoutes() {
    val fichaJugadorService = FichaJugadorService()

    routing {
        authenticate("auth-jwt") {
            route("/ficha-jugador") {
                get("/jugador/{idJugador}") {
                    val idJugador = call.parameters["idJugador"]?.toIntOrNull()

                    if (idJugador == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID inválido")
                        return@get
                    }

                    val fichaJugador = fichaJugadorService.getFichaJugadorByIdJugador(idJugador)

                    call.respond(HttpStatusCode.OK, fichaJugador)
                }

                get("/equipo/{idEquipo}") {
                    val idEquipo = call.parameters["idEquipo"]?.toIntOrNull()

                    if (idEquipo == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID inválido")
                        return@get
                    }

                    val fichaJugadores = fichaJugadorService.getFichasJugadoresEquipo(idEquipo)
                    call.respond(HttpStatusCode.OK, fichaJugadores)
                }

                post {
                    val request = call.receive<FichaJugadorRequest>()
                    val idJugador = request.idJugador
                    val idEquipo = request.idEquipo
                    val piernaHabil = request.piernaHabil
                    val caracteristicasFisicas = request.caracteristicasFisicas
                    val caracteristicasTacticas = request.caracteristicasTacticas
                    val caracteristicasTecnicas = request.caracteristicasTecnicas
                    val conductaEntrenamiento = request.conductaEntrenamiento
                    val conductaConCompañeros = request.conductaConCompañeros
                    val observacionFinal = request.observacionFinal

                    val fichaJugador = fichaJugadorService.createdFichaJugador(idJugador, idEquipo, piernaHabil, caracteristicasFisicas, caracteristicasTacticas, caracteristicasTecnicas, conductaEntrenamiento, conductaConCompañeros, observacionFinal)
                    call.respond(HttpStatusCode.OK, fichaJugador.toDTO())
                }

                delete("/{id}"){
                    val id = call.parameters["id"]?.toIntOrNull()

                    val fichaJugadorDelete = id?.let { fichaJugadorService.deleteFichaJugador(it) } ?: false
                    if (fichaJugadorDelete) {
                        call.respond(HttpStatusCode.OK, "Ficha Jugador eliminada correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Ficha Jugador no encontrado")
                    }
                }
            }
        }
    }
}