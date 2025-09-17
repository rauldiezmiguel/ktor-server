package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.ClubDAO
import services.ClubService

fun Application.clubRoutes() {
    val clubService = ClubService()

    routing {
        route("/clubes") {
            post {
                val request = call.receive<Map<String, String>>()
                val nombreClub = request["nombreClub"] ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Falta nombre de usuario"
                )
                val direccion = request["direccion"]
                val telefono = request["telefono"]
                val localizacion = request["localizacion"]

                val club = clubService.createClub(nombreClub, direccion, telefono, localizacion)
                call.respond(HttpStatusCode.Created, "Club creado con ID: ${club.id}")
            }

            get {
                val clubes = clubService.getAllClubs()
                call.respond(clubes.map { it.toDTO() })// Convertimos a JSON
            }
        }
        authenticate("auth-jwt") {
            route("/clubes") {

                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val club = id?.let { clubService.getClubById(it) }

                    if (club != null) {
                        call.respond(HttpStatusCode.OK, club.toDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Club no encontrado")
                    }
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        "ID inválido"
                    )
                    val request = call.receive<Map<String, String>>()

                    val nombreClub = request["nombreClub"]
                    val direccion = request["direccion"]
                    val telefono = request["telefono"]
                    val localizacion = request["localizacion"]

                    val clubUpdate = clubService.updateClub(id, nombreClub, direccion, telefono, localizacion)
                    if (clubUpdate) {
                        call.respond(HttpStatusCode.OK, "Club actualizado")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Club no encontrado")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val clubDeleted = id?.let { clubService.deleteClub(it) }

                    if (clubDeleted == true) {
                        call.respond(HttpStatusCode.OK, "Club eliminado")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Club no encontrado")
                    }
                }
            }
        }
    }
}