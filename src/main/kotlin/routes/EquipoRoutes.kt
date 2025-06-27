package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.EquipoService

fun Application.equipoRoutes() {
    val equipoService = EquipoService()

    routing {
        route("/equipos") {
            authenticate("auth-jwt") {
                get("/clubes/{idClub}") {
                    val idClub = call.parameters["idClub"]?.toIntOrNull()

                    if (idClub == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID club invalido")
                        return@get
                    }
                    call.respond(equipoService.getEquiposTemporadaActivaByClub(idClub).map { it.toDTO() })
                }

                get("/{id}"){
                    val id = call.parameters["id"]?.toIntOrNull()
                    val equipo = id?.let { equipoService.getEquipoById(it) }

                    if (equipo != null) {
                        call.respond(equipo.toDTO())
                    } else {
                        call.respond("Equipo no encontrado")
                    }
                }

                get("/{id}/categoria") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val equipo = id?.let { equipoService.getEquipoById(it) }
                    if (equipo != null) {
                        call.respond(HttpStatusCode.OK, equipo.categoria)
                    }else {
                        call.respond(HttpStatusCode.BadRequest, "ID invalido")
                    }
                }

                post {
                    val request = call.receive<Map<String, String>>()

                    val nombreEquipo = request["nombreEquipo"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta nombre del equipo")
                    val categoria = request["categoria"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta la categoria del equipo")
                    val subcategoria = request["subcategoria"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta la subcategoría del equipo")
                    val idClub = request["idClub"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest, "ID club invalido")
                    val idTemporada = request["idTemporada"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest, "ID temporada invalido")

                    val equipo = equipoService.createEquipo(nombreEquipo, categoria, subcategoria, idClub, idTemporada)
                    call.respond(HttpStatusCode.Created, "Nuevo equipo creado con ID: ${equipo.id}")
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val request = call.receive<Map<String, String>>()

                    val nombreEquipo = request["nombreEquipo"] ?: error("El nombre del equipo no puede ser nulo")
                    val categoria = request["categoria"] ?: error("La categoria no puede ser nula")

                    val equipoUpdate = equipoService.updateEquipo(id, nombreEquipo, categoria)
                    if (equipoUpdate) {
                        call.respond(HttpStatusCode.OK, "Equipo actualizado")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Equipo no encontrado")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val equipoDeleted = id?.let { equipoService.deleteEquipo(it) }

                    if (equipoDeleted == true) {
                        call.respond(HttpStatusCode.OK, "Equipo eliminado")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Equipo no encontrado")
                    }
                }
            }
        }
    }
}